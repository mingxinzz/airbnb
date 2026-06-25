[TOC]

# Airbnb 民宿数据分析项目 Spring Boot 接口详细设计

## 目录

1. 引言
   1.1 编写目的
   1.2 设计范围
2. 接口总体设计
   2.1 接口层定位
   2.2 接口设计原则
3. 统一返回结构设计
   3.1 通用返回结构
   3.2 状态码约定
   3.3 字段命名约定
4. 接口模块划分设计
   4.1 接口分组总览
   4.2 控制器与数据表映射
5. 自动化任务接口详细设计
   5.1 文件上传接口
   5.2 开始处理接口
   5.3 查询任务状态接口
   5.4 查询任务日志接口
   5.5 后端远程调用配置与上传分析流程
6. 首页总览接口详细设计
   6.1 获取首页总览数据接口
7. 房源分析接口详细设计
   7.1 获取区域房源数量接口
   7.2 获取房型占比接口
   7.3 获取房东房源 TopN 接口
8. 价格分析接口详细设计
   8.1 获取区域平均价格接口
   8.2 获取价格与评论关系接口
   8.3 获取日历价格趋势接口
9. 评论分析接口详细设计
   9.1 获取月度评论趋势接口
   9.2 获取评论词云接口（当前版本不实现）
10. 异常处理详细设计
      10.1 参数异常处理
      10.2 空结果处理
      10.3 系统异常处理
11. 后端代码结构建议
      11.1 Controller 层建议
      11.2 Service 层建议
      11.3 Mapper 层建议
      11.4 DTO 设计建议
12. 前后端联调约定
      12.1 时间字段约定
      12.2 数值字段约定
      12.3 任务状态字段约定
13. 接口设计总结

## 1. 引言

### 1.1 编写目的

本文档用于明确 Airbnb 民宿数据分析项目中 Spring Boot 接口层的详细设计方案。接口层位于 MySQL 结果库与 Vue3 前端页面之间，负责两类工作：第一，提供首页总览、房源分析、价格分析、评论分析等结果查询接口；第二，提供自动化任务的文件上传、任务启动、状态查询和日志查询接口。本文档重点说明接口分组、请求方式、请求参数、响应结构以及与 Hive 结果表和 MySQL 表之间的对应关系。

### 1.2 设计范围

本文档覆盖当前项目所需的全部后端接口，范围包括自动化任务接口、首页总览接口、房源分析接口、价格分析接口和评论分析接口。当前页面已经删除地图分析页，因此本文档不再保留地图相关接口。接口设计需要与《Hive 建表与离线分析 SQL 详细设计》和《MySQL 表设计》保持一致，不单独捏造无来源字段。

## 2. 接口总体设计

### 2.1 接口层定位

Spring Boot 接口层在本项目中的定位是应用服务层。它不直接执行 Hive 统计计算，也不直接读取原始 CSV 文件，而是读取 MySQL 中已经准备好的结果表与任务控制表，并将其转换为前端可直接消费的 JSON 数据。对于自动化任务，Spring Boot 负责受理请求、创建任务记录、调度后续处理流程并反馈任务状态，但不在 HTTP 请求线程中同步跑完整的大数据分析链路。

### 2.2 接口设计原则

接口设计遵循以下原则。第一，统一前缀原则，所有接口统一使用 `/api` 前缀。第二，主题分组原则，按页面主题组织接口路径，例如 `dashboard`、`listing`、`price`、`review`、`pipeline`。第三，结果直出原则，接口返回结构尽量贴合前端图表需要，减少额外转换。第四，职责分离原则，文件上传与开始处理拆成两个接口，便于定位问题。第五，口径一致原则，字段命名和统计含义必须与 Hive 结果表和 MySQL 表保持一致。

## 3. 统一返回结构设计

### 3.1 通用返回结构

建议所有接口统一返回如下结构：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

其中 `code` 表示业务状态码，`message` 表示结果说明，`data` 表示实际业务数据。对于列表型接口，`data` 可以是数组；对于总览型接口，`data` 可以是对象。

### 3.2 状态码约定

当前项目建议使用简化状态码体系：`200` 表示请求成功，`400` 表示参数错误，`404` 表示未查询到结果，`500` 表示系统内部错误。课程项目阶段不需要扩展更复杂的业务码，但前后端必须严格统一。

### 3.3 字段命名约定

数据库表字段仍可使用下划线风格，例如 `review_month`、`avg_price`、`data_stat_date`，但接口返回给前端的 JSON 字段统一使用驼峰命名，例如 `reviewMonth`、`avgPrice`、`dataStatDate`。这样更符合 Vue3 前端使用习惯。

## 4. 接口模块划分设计

### 4.1 接口分组总览

接口分为五组。第一组是 `pipeline` 自动化任务接口，负责文件上传、任务启动、状态查询和日志查询。第二组是 `dashboard` 首页总览接口，负责输出首页核心指标。第三组是 `listing` 房源分析接口，负责输出区域房源数量、房型占比和房东房源 TopN。第四组是 `price` 价格分析接口，负责输出区域平均价格、价格与评论关系、日历价格趋势。第五组是 `review` 评论分析接口，负责输出月度评论趋势和评论词云。

### 4.2 控制器与数据表映射

建议控制器与表的关系如下：

1. `PipelineController`
   对应 `pipeline_task`、`pipeline_task_log`
2. `DashboardController`
   对应 `dashboard_overview`
3. `ListingController`
   对应 `region_house_count`、`room_type_ratio`、`host_house_topn`
4. `PriceController`
   对应 `region_avg_price`、`price_review_relation`、`calendar_price_trend`
5. `ReviewController`
   对应 `monthly_review_trend`
   > 注：`review_word_freq` 对应的评论词云接口在当前版本中保留文档占位，不进入实现范围。

## 5. 自动化任务接口详细设计

### 5.1 文件上传接口

1. 接口基本信息  
   1. 接口名称：文件上传接口  
   2. 请求方式：`POST`  
   3. 请求路径：`/api/pipeline/upload`  
   4. 请求类型：`multipart/form-data`

2. 接口用途说明  
   该接口只负责接收 `listings.csv`、`calendar.csv` 和 `reviews.csv` 三个文件，完成文件非空校验、文件名校验、本地保存和任务记录创建。该接口不直接启动 Hadoop、Hive 或 Sqoop 处理流程。这样设计后，前端可以先确认文件上传是否成功，再显式发起“开始处理”请求，定位问题更清楚。

3. 详细功能说明  
   该接口的职责是完成自动化任务的“准备阶段”。它面向前端上传组件，接收用户选择的三个原始数据文件，并在后端完成基础校验、任务编号生成和本地任务目录落盘。该接口成功返回后，只能说明“文件已经被系统接收并保存”，不能说明大数据分析已经开始执行，也不能说明分析结果已经生成。因此，前端在调用完该接口后，还需要继续调用“开始处理接口”来正式启动后续的 HDFS、Hive 和 Sqoop 链路。

4. 请求参数设计  
   1. `listingsFile`：`MultipartFile`，必填，对应 `listings.csv`  
   2. `calendarFile`：`MultipartFile`，必填，对应 `calendar.csv`  
   3. `reviewsFile`：`MultipartFile`，必填，对应 `reviews.csv`

5. 后端处理逻辑  
   1. 校验三个文件是否齐全  
   2. 校验文件名是否符合预期  
   3. 创建唯一 `taskId`  
   4. 将文件保存到本地任务目录  
   5. 向 `pipeline_task` 写入任务记录，初始状态写入 `CREATED`

6. 返回字段设计  
   1. `taskId`：任务编号  
   2. `taskStatus`：任务状态  
   3. `currentStage`：当前阶段  
   4. `message`：阶段说明  
   5. `logFile`：日志文件路径  
   6. `startTime`：开始时间  
   7. `endTime`：结束时间  
   8. `errorMessage`：失败摘要

7. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "TASK202606200001",
    "taskStatus": "CREATED",
    "currentStage": "CREATED",
    "message": "文件上传完成，任务已创建",
    "logFile": "/home/ubuntu/workspace/airbnb/logs/TASK202606200001.log",
    "startTime": null,
    "endTime": null,
    "errorMessage": null
  }
}
```

### 5.2 开始处理接口

1. 接口基本信息  
   1. 接口名称：开始处理接口  
   2. 请求方式：`POST`  
   3. 请求路径：`/api/pipeline/{taskId}/start`

2. 接口用途说明  
   该接口用于在文件上传完成后，正式启动自动化分析流程。后端接到请求后应校验 `taskId` 是否存在、任务文件是否齐全、任务是否已启动，然后再异步调度大数据分析脚本或远程执行大数据服务器上的总控脚本。

3. 详细功能说明  
   该接口的职责是完成自动化任务的“执行启动阶段”。它不负责接收文件，而是基于已经存在的 `taskId` 去启动后台分析流程。调用成功后，Spring Boot 后端应立即返回任务已进入运行状态，而不是阻塞等待大数据分析全部结束。前端拿到返回结果后，应切换为任务轮询模式，通过状态接口持续查询当前阶段，直到任务进入 `FINISHED` 或 `FAILED`。

4. 路径参数设计  
   1. `taskId`：`String`，必填，任务编号

5. 后端处理逻辑  
   1. 校验任务记录是否存在  
   2. 校验任务状态是否允许启动  
   3. 将 `taskStatus` 更新为 `RUNNING`  
   4. 将 `currentStage` 更新为 `UPLOAD_RAW_TO_HDFS`  
   5. 将 `message` 更新为“开始上传原始文件到 HDFS”  
   6. 异步执行后续任务链路

6. 返回字段设计  
   字段与文件上传接口一致，但状态应切换为处理中的状态。

7. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "TASK202606200001",
    "taskStatus": "RUNNING",
    "currentStage": "UPLOAD_RAW_TO_HDFS",
    "message": "开始上传原始文件到 HDFS",
    "logFile": "/home/ubuntu/workspace/airbnb/logs/TASK202606200001.log",
    "startTime": "2026-06-20 21:30:03",
    "endTime": null,
    "errorMessage": null
  }
}
```

### 5.3 查询任务状态接口

1. 接口基本信息  
   1. 接口名称：查询任务状态接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/pipeline/{taskId}/status`

2. 接口用途说明  
   该接口用于前端轮询任务处理进度。数据来源为 `pipeline_task` 表。当前返回口径与总控脚本日志保持一致，总体状态由 `taskStatus` 表示，执行阶段由 `currentStage` 表示，阶段说明由 `message` 表示。

3. 详细功能说明  
   该接口是自动化任务页面的核心接口。前端在启动任务后，需要周期性调用该接口来刷新任务状态。后端通过读取 `pipeline_task` 表中的最新记录，将当前任务的总体状态、当前执行阶段、阶段说明、时间信息和失败摘要返回给前端。前端据此可以展示“任务是否已开始”“当前执行到哪一步”“是否已经完成”“如果失败是在哪个阶段失败”等关键信息。该接口不返回明细日志，只返回状态摘要。

4. 路径参数设计  
   1. `taskId`：`String`，必填，任务编号

5. 返回字段设计  
   1. `taskId`：任务编号  
   2. `taskStatus`：`CREATED / RUNNING / FINISHED / FAILED`  
   3. `currentStage`：`CREATED / UPLOAD_RAW_TO_HDFS / CLEAN_DATA / UPLOAD_CLEAN_TO_HDFS / HIVE_DWD / HIVE_ADS / SQOOP_EXPORT / FINISHED / FAILED`  
   4. `message`：阶段说明  
   5. `logFile`：日志文件路径  
   6. `submittedAt`：提交时间  
   7. `startTime`：开始时间  
   8. `endTime`：结束时间  
   9. `errorMessage`：失败摘要

6. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "TASK202606200001",
    "taskStatus": "RUNNING",
    "currentStage": "HIVE_ADS",
    "message": "正在执行 Hive ADS 分析",
    "logFile": "/home/ubuntu/workspace/airbnb/logs/TASK202606200001.log",
    "submittedAt": "2026-06-20 21:30:00",
    "startTime": "2026-06-20 21:30:03",
    "endTime": null,
    "errorMessage": null
  }
}
```

### 5.4 查询任务日志接口

1. 接口基本信息  
   1. 接口名称：查询任务日志接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/pipeline/{taskId}/log`

2. 接口用途说明  
   该接口用于查询任务日志明细或日志摘要，数据来源为 `pipeline_task_log` 表，也可结合日志文件内容进行摘要返回。主要供前端任务状态页展示任务执行细节。

3. 详细功能说明  
   该接口用于补充状态接口无法表达的细节信息。状态接口只能说明当前阶段和总体状态，而日志接口可以展示每一步的执行过程，例如“开始上传原始文件到 HDFS”“Hive ADS 分析完成”“分析结果导出到 MySQL 完成”等。当前端需要展示任务时间线、失败阶段详情或运维排查信息时，应调用该接口。该接口通常与状态接口配合使用，而不是替代状态接口。

4. 路径参数设计  
   1. `taskId`：`String`，必填，任务编号

5. 返回字段设计  
   日志接口返回对象数组，字段与总控脚本日志口径一致，便于前端按阶段展示。

6. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "logTime": "2026-06-20 21:30:01",
      "level": "INFO",
      "stage": "UPLOAD_RAW_TO_HDFS",
      "status": "RUNNING",
      "message": "开始上传原始文件到 HDFS"
    },
    {
      "logTime": "2026-06-20 21:30:08",
      "level": "INFO",
      "stage": "UPLOAD_RAW_TO_HDFS",
      "status": "SUCCESS",
      "message": "原始文件上传到 HDFS 完成"
    },
    {
      "logTime": "2026-06-20 21:31:45",
      "level": "INFO",
      "stage": "HIVE_ADS",
      "status": "RUNNING",
      "message": "正在执行 Hive ADS 分析"
    }
  ]
}
```

### 5.5 后端远程调用配置与上传分析流程

1. 当前项目中，Spring Boot 后端与 Hadoop/Hive/Sqoop 所在的大数据服务器不部署在同一台机器上。因此，后端不在本机直接执行 `run_airbnb_pipeline.sh`，而是通过远程连接方式调用大数据服务器完成文件上传和脚本执行。
2. 当前大数据服务器的远程连接信息如下：
   1. `host`：`10.0.12.16`
   2. `port`：`22`
   3. `username`：`ubuntu`
   4. `baseDir`：`/home/ubuntu/workspace/airbnb`
3. 后端配置文件中应增加远程执行参数，推荐采用如下配置结构：

```yaml
airbnb:
  remote:
    enabled: true
    host: 10.0.12.16
    port: 22
    username: ubuntu
    password: 服务器登录密码
    connect-timeout-ms: 10000
    session-timeout-ms: 30000
    task-base-dir: /home/ubuntu/workspace/airbnb/tasks
    run-script: /home/ubuntu/workspace/airbnb/scripts/run_airbnb_pipeline.sh
    log-dir: /home/ubuntu/workspace/airbnb/logs
```

4. 上传分析接口建议直接由后端完成“接收文件 + 远程上传 + 远程启动”的完整动作，接口路径可统一为：

```text
POST /api/pipeline/upload-and-start
```

5. 详细功能说明  
   该流程适用于“Spring Boot 后端与大数据服务器分离部署”的实际环境。后端机器本身不运行 Hadoop、Hive 和 Sqoop，而是作为调度中心存在。用户上传文件后，后端先在远程服务器上创建任务目录，再通过 SFTP 把文件传过去，最后通过 SSH 执行总控脚本。这样可以把 Web 服务与大数据执行环境解耦，既避免在 Java 服务器上部署整套大数据组件，也便于后续将大数据环境独立扩展。

6. 该接口的后端处理流程固定如下：
   1. 接收前端上传的 `listings.csv`、`calendar.csv`、`reviews.csv`
   2. 生成唯一 `taskId`
   3. 通过 SSH 在大数据服务器上创建任务目录：

```bash
mkdir -p /home/ubuntu/workspace/airbnb/tasks/{taskId}/input
mkdir -p /home/ubuntu/workspace/airbnb/tasks/{taskId}/logs
```

   4. 通过 SFTP 将三个文件上传到：

```text
/home/ubuntu/workspace/airbnb/tasks/{taskId}/input/listings.csv
/home/ubuntu/workspace/airbnb/tasks/{taskId}/input/calendar.csv
/home/ubuntu/workspace/airbnb/tasks/{taskId}/input/reviews.csv
```

   5. 通过 SSH 执行总控脚本：

```bash
bash /home/ubuntu/workspace/airbnb/scripts/run_airbnb_pipeline.sh {taskId}
```

   6. 后端返回任务编号、当前阶段和日志文件路径，前端后续通过状态接口轮询执行进度

7. 该接口的返回结构建议如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "taskId": "202606230001",
    "taskStatus": "RUNNING",
    "currentStage": "UPLOAD_RAW_TO_HDFS",
    "message": "开始上传原始文件到HDFS",
    "logFile": "/home/ubuntu/workspace/airbnb/logs/202606230001.log"
  }
}
```

8. 对后端实现而言，这个接口只需要保证三件事：
   1. 能远程连接 `10.0.12.16:22`
   2. 能把文件上传到 `/home/ubuntu/workspace/airbnb/tasks/{taskId}/input/`
   3. 能远程执行 `run_airbnb_pipeline.sh`

9. 当前阶段采用 `IP + 用户名 + 密码` 方式进行联调，便于快速打通链路。后续正式版本可再切换为 SSH 私钥认证，但接口流程本身不发生变化。

## 6. 首页总览接口详细设计

### 6.1 获取首页总览数据接口

1. 接口基本信息  
   1. 接口名称：获取首页总览数据接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/dashboard/overview`

2. 数据来源  
   对应 MySQL 表：`dashboard_overview`

3. 详细功能说明  
   该接口用于首页总览区块，向前端提供整套分析结果的全局摘要。返回内容通常用于首页顶部的指标卡片或概览面板，例如房源总数、房东总数、评论总数和整体平均价格。它的特点是字段少、信息密度高，主要用于帮助使用者快速把握本次数据分析结果的整体规模，而不是展示细粒度明细。

4. 返回字段设计  
   1. `totalListings`：房源总数，对应 `total_listings`  
   2. `totalHosts`：房东总数，对应 `total_hosts`  
   3. `totalReviews`：评论总数，对应 `total_reviews`  
   4. `avgPrice`：平均价格，对应 `avg_price`  
   5. `activeNeighbourhoods`：活跃区域数，对应 `active_neighbourhoods`  
   6. `dataStatDate`：统计时间，对应 `data_stat_date`

5. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "totalListings": 3585,
    "totalHosts": 2181,
    "totalReviews": 68275,
    "avgPrice": 173.93,
    "activeNeighbourhoods": 25,
    "dataStatDate": "2026-06-20 22:10:00"
  }
}
```

## 7. 房源分析接口详细设计

### 7.1 获取区域房源数量接口

1. 接口基本信息  
   1. 接口名称：获取区域房源数量接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/listing/region-count`

2. 数据来源  
   对应 MySQL 表：`region_house_count`

3. 详细功能说明  
   该接口用于展示不同区域的房源数量分布，是房源供给结构分析的基础接口。前端通常将该接口结果绘制为柱状图或排序图，用于体现各区域的房源多少和区域间差异。对于运营分析人员，该接口可以反映平台供给是否集中；对于查看房源的用户，该接口可以帮助其判断哪些区域房源更丰富、选择空间更大。

4. 返回字段设计  
   1. `neighbourhoodCleansed`：区域名称，对应 `neighbourhood_cleansed`  
   2. `houseCount`：房源数量，对应 `house_count`  
   3. `rankingNo`：排序号，对应 `ranking_no`  
   4. `dataStatDate`：统计时间，对应 `data_stat_date`

5. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "neighbourhoodCleansed": "Jamaica Plain",
      "houseCount": 343,
      "rankingNo": 1,
      "dataStatDate": "2026-06-20 22:10:00"
    },
    {
      "neighbourhoodCleansed": "South End",
      "houseCount": 335,
      "rankingNo": 2,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

### 7.2 获取房型占比接口

1. 接口基本信息  
   1. 接口名称：获取房型占比接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/listing/room-type-ratio`

2. 数据来源  
   对应 MySQL 表：`room_type_ratio`

3. 详细功能说明  
   该接口用于展示不同房型在平台房源中的数量和占比，例如整套房、独立房间、合住房间等。前端通常将该接口结果绘制为饼图或环形图，并可辅以数量标签。它的主要用途是反映平台供给结构，帮助使用者理解当前市场中哪类房型占主导地位。

4. 返回字段设计  
   1. `roomType`：房型，对应 `room_type`  
   2. `houseCount`：房源数量，对应 `house_count`  
   3. `ratio`：占比，对应 `ratio`  
   4. `dataStatDate`：统计时间，对应 `data_stat_date`

5. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "roomType": "Entire home/apt",
      "houseCount": 2127,
      "ratio": 0.5933,
      "dataStatDate": "2026-06-20 22:10:00"
    },
    {
      "roomType": "Private room",
      "houseCount": 1378,
      "ratio": 0.3844,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

### 7.3 获取房东房源 TopN 接口

1. 接口基本信息  
   1. 接口名称：获取房东房源 TopN 接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/listing/host-topn`

2. 数据来源  
   对应 MySQL 表：`host_house_topn`

3. 详细功能说明  
   该接口用于展示房东经营规模排名，即平台中房源数量最多的房东列表。前端通常将该接口结果绘制为 TopN 柱状图。该接口适合用于识别平台中是否存在明显的规模化经营者，也可以辅助运营人员分析平台供给主体是分散还是集中。

4. 可选请求参数  
   1. `limit`：`Integer`，非必填，默认 `10`

5. 返回字段设计  
   1. `hostId`：房东 ID，对应 `host_id`  
   2. `hostName`：房东名称，对应 `host_name`  
   3. `houseCount`：房源数量，对应 `house_count`  
   4. `rankingNo`：排序号，对应 `ranking_no`  
   5. `dataStatDate`：统计时间，对应 `data_stat_date`

6. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "hostId": 30283594,
      "hostName": "Kara",
      "houseCount": 324,
      "rankingNo": 1,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

## 8. 价格分析接口详细设计

### 8.1 获取区域平均价格接口

1. 接口基本信息  
   1. 接口名称：获取区域平均价格接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/price/region-avg`

2. 数据来源  
   对应 MySQL 表：`region_avg_price`

3. 详细功能说明  
   该接口用于展示不同区域的平均价格、最低价格、最高价格和样本量，是区域价格层次分析的核心接口。前端通常将该接口结果绘制为柱状图或对比表格。通过该接口可以判断高价区域、中价区域和低价区域，为住宿选择和区域经营分析提供依据。

4. 返回字段设计  
   1. `neighbourhoodCleansed`：区域名称，对应 `neighbourhood_cleansed`  
   2. `avgPrice`：平均价格，对应 `avg_price`  
   3. `minPrice`：最低价格，对应 `min_price`  
   4. `maxPrice`：最高价格，对应 `max_price`  
   5. `houseCount`：样本数量，对应 `house_count`  
   6. `dataStatDate`：统计时间，对应 `data_stat_date`

5. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "neighbourhoodCleansed": "Back Bay",
      "avgPrice": 237.56,
      "minPrice": 85.00,
      "maxPrice": 1250.00,
      "houseCount": 302,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

### 8.2 获取价格与评论关系接口

1. 接口基本信息  
   1. 接口名称：获取价格与评论关系接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/price/review-scatter`

2. 数据来源  
   对应 MySQL 表：`price_review_relation`

3. 详细功能说明  
   该接口用于展示价格与评论数量之间的关系，通常供前端绘制散点图。每条记录代表一个房源样本，包含房源价格、评论数量、评分和区域信息。它不用于展示聚合后的单一统计值，而是用于观察样本分布特征，例如中低价房源是否更容易积累评论，高价房源是否评论较少。

4. 可选请求参数  
   1. `limit`：`Integer`，非必填，默认 `500`

5. 返回字段设计  
   1. `listingId`：房源 ID，对应 `listing_id`  
   2. `neighbourhoodCleansed`：区域名称，对应 `neighbourhood_cleansed`  
   3. `roomType`：房型，对应 `room_type`  
   4. `price`：价格，对应 `price`  
   5. `numberOfReviews`：评论数量，对应 `number_of_reviews`  
   6. `reviewScoresRating`：评分，对应 `review_scores_rating`  
   7. `dataStatDate`：统计时间，对应 `data_stat_date`

6. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "listingId": 12147973,
      "neighbourhoodCleansed": "Roslindale",
      "roomType": "Entire home/apt",
      "price": 250.0,
      "numberOfReviews": 0,
      "reviewScoresRating": null,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

### 8.3 获取日历价格趋势接口

1. 接口基本信息  
   1. 接口名称：获取日历价格趋势接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/price/trend`

2. 数据来源  
   对应 MySQL 表：`calendar_price_trend`

3. 详细功能说明  
   该接口用于展示价格在时间维度上的波动趋势，通常以月份为单位返回统计结果。前端通常将该接口绘制为折线图，用于展示价格在不同时间段的上升、下降和波动情况。该接口既可以支持平台价格趋势观察，也可以支持用户从时间维度理解价格变化。

4. 返回字段设计  
   1. `statDate`：统计时间，对应 `stat_date`  
   2. `avgPrice`：平均价格，对应 `avg_price`  
   3. `sampleCount`：样本数量，对应 `sample_count`  
   4. `dataStatDate`：统计时间，对应 `data_stat_date`

5. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "statDate": "2016-09",
      "avgPrice": 186.42,
      "sampleCount": 102345,
      "dataStatDate": "2026-06-20 22:10:00"
    },
    {
      "statDate": "2016-10",
      "avgPrice": 192.37,
      "sampleCount": 108765,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

## 9. 评论分析接口详细设计

### 9.1 获取月度评论趋势接口

1. 接口基本信息  
   1. 接口名称：获取月度评论趋势接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/review/month-trend`

2. 数据来源  
   对应 MySQL 表：`monthly_review_trend`

3. 详细功能说明  
   该接口用于展示评论数量随时间变化的趋势，通常以月份为单位进行聚合统计。前端通常将该接口结果绘制为折线图。它可以帮助使用者识别评论高峰期和低谷期，从侧面反映平台活跃度和用户入住活跃程度的变化。

4. 返回字段设计  
   1. `reviewMonth`：评论月份，对应 `review_month`  
   2. `reviewCount`：评论数量，对应 `review_count`  
   3. `dataStatDate`：统计时间，对应 `data_stat_date`

5. 返回示例如下：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "reviewMonth": "2015-07",
      "reviewCount": 1280,
      "dataStatDate": "2026-06-20 22:10:00"
    },
    {
      "reviewMonth": "2015-08",
      "reviewCount": 1465,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

### 9.2 获取评论词云接口（当前版本不实现）

> 注：该接口对应的 `review_word_freq` 表属于评论文本词频统计结果，需额外完成英文分词、停用词过滤和词频计算流程。当前版本的自动化主链不包含这部分文本处理能力，因此该接口和对应功能暂不实现，前后端联调时不纳入开发与验收范围。

1. 接口基本信息  
   1. 接口名称：获取评论词云接口  
   2. 请求方式：`GET`  
   3. 请求路径：`/api/review/wordcloud`

2. 数据来源  
   对应 MySQL 表：`review_word_freq`

3. 详细功能说明  
   该接口原本用于返回评论文本中的高频词结果，供前端绘制评论词云或高频词 TopN 图表。由于该功能需要额外增加评论文本清洗、英文分词和停用词过滤流程，当前版本未纳入自动化主链，也不进入本阶段开发范围。因此，这一节仅保留文档占位作用，便于后续版本扩展。

4. 可选请求参数  
   1. `limit`：`Integer`，非必填，默认 `100`

5. 返回字段设计  
   1. `rankingNo`：排序号，对应 `ranking_no`  
   2. `word`：词语，对应 `word`  
   3. `freq`：词频，对应 `freq`  
   4. `dataStatDate`：统计时间，对应 `data_stat_date`

6. 口径说明  
   该接口来源于 `reviews.csv.comments` 的英文评论文本，因此词云统计采用英文文本清洗和英文停用词过滤，不采用中文分词逻辑。

7. 实现状态说明  
   当前版本不实现该接口，不要求 Spring Boot 提供 `/api/review/wordcloud` 的实际可调用能力，也不要求大数据脚本生成 `review_word_freq` 数据。

8. 返回示例如下（仅作预留格式说明，不进入当前版本实现）：

```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "rankingNo": 1,
      "word": "great",
      "freq": 1865,
      "dataStatDate": "2026-06-20 22:10:00"
    },
    {
      "rankingNo": 2,
      "word": "location",
      "freq": 1742,
      "dataStatDate": "2026-06-20 22:10:00"
    }
  ]
}
```

## 10. 异常处理详细设计

### 10.1 参数异常处理

对于文件上传接口，应重点校验文件是否缺失、文件名是否错误、文件是否为空。对于 `limit` 等数值参数，应校验是否为正整数。参数错误统一返回 `400`。

### 10.2 空结果处理

如果结果表存在但当前没有数据，接口不建议直接抛异常，而应返回空数组或空对象，并通过 `message` 或前端空态提示说明“当前无可展示结果”。

### 10.3 系统异常处理

数据库连接失败、脚本执行异常、结果读取失败等情况统一返回 `500`。后端日志保留详细堆栈，前端只展示简要错误信息。

## 11. 后端代码结构建议

### 11.1 Controller 层建议

建议按主题拆分控制器：`PipelineController`、`DashboardController`、`ListingController`、`PriceController`、`ReviewController`。

### 11.2 Service 层建议

Service 层负责两类逻辑：一类是 MySQL 结果查询，另一类是自动化任务编排。二者不建议混在同一个 Service 中。

### 11.3 Mapper 层建议

每张结果表和每张任务表尽量有明确的 Mapper 映射，查询语句保持简单直观，不在 Mapper 层堆叠复杂业务逻辑。

### 11.4 DTO 设计建议

建议统一使用 DTO 输出给前端，不直接暴露数据库实体对象。这样便于控制字段命名和返回结构。

## 12. 前后端联调约定

### 12.1 时间字段约定

月份类字段统一返回 `yyyy-MM`，完整时间统一返回 `yyyy-MM-dd HH:mm:ss`。

### 12.2 数值字段约定

价格、数量、频次等字段统一返回数值类型，不返回带单位或货币符号的字符串。

### 12.3 任务状态字段约定

前端应按 `taskStatus` 和 `currentStage` 两层口径处理任务状态，不要把阶段值误当成总体状态值使用。当前项目的正式口径如下：

1. `taskStatus` 只使用：`CREATED`、`RUNNING`、`FINISHED`、`FAILED`
2. `currentStage` 只使用：`CREATED`、`UPLOAD_RAW_TO_HDFS`、`CLEAN_DATA`、`UPLOAD_CLEAN_TO_HDFS`、`HIVE_DWD`、`HIVE_ADS`、`SQOOP_EXPORT`、`FINISHED`、`FAILED`
3. `message` 用于表达阶段说明，例如“开始上传原始文件到 HDFS”“正在执行 Hive ADS 分析”“分析结果导出到 MySQL 完成”

## 13. 接口设计总结

当前接口设计已经与现有页面范围保持一致，覆盖了自动化任务、首页总览、房源分析、价格分析和评论分析五类接口，并与 Hive 结果层和 MySQL 表设计保持对齐。自动化任务接口已拆分为“文件上传”和“开始处理”两个步骤，更便于联调、排错和验收展示。
