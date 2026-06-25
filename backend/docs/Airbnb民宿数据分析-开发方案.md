# Airbnb 民宿数据分析项目开发方案

## 目录

1. 文档定位
2. 当前已知条件与方案假设
   2.1 已知条件
   2.2 本方案采用的关键假设
3. 总体建设目标
4. 技术路线
   4.1 总体技术选型
   4.2 为什么这样选
5. 系统总体架构
6. 数据集理解
   6.1 数据文件说明
   6.2 推荐分析主题
7. 分层设计
   7.1 ODS 原始层
   7.2 ODS 清洗层
   7.3 DWD 明细层
   7.4 ADS 应用层
8. 数据清洗设计
   8.1 `listings.csv` 清洗规则
   8.2 `calendar.csv` 清洗规则
   8.3 `reviews.csv` 清洗规则
   8.4 清洗实现建议
9. Hive 数仓与分析设计
   9.1 Hive 建表建议
   9.2 重点分析指标
   9.3 推荐对应图表
10. Web 系统设计
   10.1 架构模式
   10.2 页面规划
   10.3 前端目录建议
   10.4 后端目录建议
11. 数据库设计
   11.1 MySQL 作用
   11.2 推荐表设计
12. 接口设计
   12.1 接口原则
   12.2 推荐接口清单
13. 项目实施步骤
   13.1 第一阶段：环境确认
   13.2 第二阶段：数据接入
   13.3 第三阶段：数据清洗
   13.4 第四阶段：Hive 建模与分析
   13.5 第五阶段：结果同步 MySQL
   13.6 第六阶段：后端开发
   13.7 第七阶段：Vue3 前端开发
   13.8 第八阶段：测试与答辩材料整理
14. 推荐项目目录结构
15. 时间安排建议
16. 风险与应对
17. 最终交付清单
18. 建议答辩讲解顺序
19. 本方案的推荐落地版本
20. 下一步建议

## 1. 文档定位

本文档用于指导完成“大数据实训作业：Airbnb 民宿数据分析”项目的设计、开发、联调、演示与答辩。

## 2. 当前已知条件与方案假设

### 2.1 已知条件

- 你已在本地 Windows 上通过 VMware 搭建三台 Linux 虚拟机，结构为一主两从。
- Hadoop、SSH 等基础大数据环境已经配置完成。
- 当前工作目录中已经具备以下原始数据文件：
  - `listings.csv`
  - `calendar.csv`
  - `reviews.csv`
- 课程要求包含：
  - 离线/批处理类大数据分析
  - Web 端展示
  - 至少 5 种可视化图表
  - 数据清洗、存储、分析、展示的完整链路

### 2.2 本方案采用的关键假设

1. 本次实训以现有 CSV 数据为主，不再把“网页爬虫采集”作为核心交付内容，只保留一个“数据接入脚本”作为采集入口的替代实现。
2. Web 端按你的要求优先采用 `Vue3`，后端采用 `Spring Boot` 提供 API。
3. 本项目仅实现离线类大数据分析，不再设计实时处理链路。
4. 若你当前虚拟机尚未安装 MySQL、Hive、Sqoop，则本方案把它们视为项目部署项。

## 3. 总体建设目标

围绕 Boston Airbnb 开放数据，构建一个完整的数据分析平台，完成以下目标：

- 将原始 CSV 数据接入 HDFS。
- 对数据进行清洗、标准化、缺失值处理与字段规整。
- 基于 Hive 建立数仓分析表并输出分析结果。
- 将分析结果同步到 MySQL。
- 通过 `Spring Boot + Vue3 + ECharts` 构建可视化 Web 系统。
- 形成可演示、可答辩、可截图的完整项目材料。

## 4. 技术路线

## 4.1 总体技术选型

### 数据层

- 分布式存储：`HDFS`
- 离线计算：`MapReduce + Hive`
- 关系型结果库：`MySQL`
- 数据导出：`Sqoop`

### 服务层

- 后端接口：`Spring Boot`
- 数据访问：`MyBatis` 或 `MyBatis-Plus`
- 接口风格：`RESTful JSON API`

### 前端展示层

- 前端框架：`Vue3`
- 构建工具：`Vite`
- UI 组件库：`Element Plus`
- 图表库：`ECharts`
- 网络请求：`Axios`
- 路由：`Vue Router`
- 状态管理：`Pinia`

## 4.2 为什么这样选

- `Vue3` 适合快速完成课程项目页面，生态成熟，和 ECharts 集成简单。
- `Spring Boot` 比 Flask 更适合和 Vue3 形成前后端分离结构，也更贴近题目中提到的 Java 技术栈。
- `Hive` 适合课程场景下完成统计分析 SQL，开发效率高，答辩时也容易解释。
- `MySQL` 负责承接最终报表数据，避免前端直接访问 Hive。
- `Spring Boot` 在本方案中不仅负责结果查询接口，还承担自动化任务编排职责，用于接收上传文件、创建任务、异步调用主节点脚本并回传状态。

## 5. 系统总体架构

```text
原始CSV数据
   ↓
Vue3上传页面
   ↓
Spring Boot 上传接口 + 任务调度
   ↓
主节点自动化总控脚本
   ↓
HDFS 原始层 ods_raw
   ↓
MapReduce 数据清洗
   ↓
HDFS 清洗层 ods_clean
   ↓
Hive 建表 + 分析计算
   ↓
Hive 结果表 ads_result
   ↓
Sqoop 导出
   ↓
MySQL 应用库
   ↓
Spring Boot REST API
   ↓
Vue3 + ECharts 可视化平台
```

补充说明：本方案已按自动化验收要求调整。人工只负责在页面上传文件并提交分析任务，不再允许在 Linux 终端手动执行 `hdfs dfs -put`、`hadoop jar`、`hive -f` 或 `sqoop export`。这些步骤统一由 Spring Boot 异步触发主节点脚本自动完成。

## 6. 数据集理解

## 6.1 数据文件说明

### `listings.csv`

核心房源表，重点分析对象。通常包含：

- 房源 ID
- 房东 ID / 房东名称
- 社区或区域
- 经纬度
- 房型
- 价格
- 最少入住天数
- 评论数量
- 可用天数
- 评分相关字段

### `calendar.csv`

按日期记录房源可用性与价格，适合分析：

- 时间维度价格变化
- 房源日历可订情况
- 节假日或旺季价格趋势

### `reviews.csv`

评论明细表，适合分析：

- 评论时间趋势
- 活跃房源分布
- 用户评价热度
- 评论词云

## 6.2 推荐分析主题

建议围绕以下业务问题组织项目：

1. 什么区域的房源数量最多？
2. 什么房型最常见？
3. 哪些区域的平均价格最高？
4. 房源价格与评论数是否存在关系？
5. 房东是否存在“多房源经营”现象？
6. 日历价格在时间维度上如何波动？
7. 评论在哪些月份最活跃？
8. 哪些词语高频出现在评论中？

## 7. 分层设计

为了让项目结构清晰，建议采用简化数仓分层。

## 7.1 ODS 原始层

作用：保存原始导入数据，不做业务加工。

建议目录：

```text
/airbnb/ods/raw/listings/
/airbnb/ods/raw/calendar/
/airbnb/ods/raw/reviews/
```

## 7.2 ODS 清洗层

作用：完成基础数据清洗、字段规整。

建议目录：

```text
/airbnb/ods/clean/listings/
/airbnb/ods/clean/calendar/
/airbnb/ods/clean/reviews/
```

## 7.3 DWD 明细层

作用：形成结构化、可分析的明细表。

建议 Hive 表：

- `dwd_listings`
- `dwd_calendar`
- `dwd_reviews`

## 7.4 ADS 应用层

作用：面向前端页面输出专题统计结果。

建议 Hive 结果表：

- `ads_region_house_count`
- `ads_room_type_ratio`
- `ads_region_avg_price`
- `ads_price_review_relation`
- `ads_host_house_topn`
- `ads_monthly_review_trend`
- `ads_calendar_price_trend`
- `ads_review_word_freq`

## 8. 数据清洗设计

## 8.1 `listings.csv` 清洗规则

- 去除完全重复记录。
- 保留主键 `id` 非空的数据。
- 价格字段去掉 `$`、`,` 等符号，转换为数值型。
- 对空值较多的描述类字段可保留；分析核心字段空值则剔除或填充。
- 经纬度字段转换为 `double`。
- 评论数、可用天数、最少入住天数等字段转为整数。
- 异常价格过滤，例如价格小于等于 0 或极端异常值单独标记。

## 8.2 `calendar.csv` 清洗规则

- 日期字段统一成 `yyyy-MM-dd`。
- `available` 字段统一成 `Y/N` 或 `1/0`。
- 价格字段转数值。
- 清理重复的 `listing_id + date` 记录。

## 8.3 `reviews.csv` 清洗规则

- 评论时间字段转标准日期。
- 过滤空评论、乱码评论。
- 保留房源 ID 关联键。
- 对评论内容做基础文本预处理，为词云做准备：
  - 转小写
  - 去停用词
  - 去标点

## 8.4 清洗实现建议

题目要求里明确提到了 `MapReduce`，因此建议：

- 使用 `Python/Shell` 脚本完成原始文件上传 HDFS。
- 使用 `Java MapReduce` 完成清洗任务。
- 清洗结果重新写回 HDFS。

如果你希望降低开发量，可采用以下折中方式：

- `MapReduce` 重点实现一张核心表 `listings.csv` 的清洗。
- `calendar.csv`、`reviews.csv` 的清洗可通过 Hive 外表 + SQL 完成。

这样既满足课程展示点，也控制实现成本。

## 9. Hive 数仓与分析设计

## 9.1 Hive 建表建议

### 原始外部表

- `ods_listings_raw`
- `ods_calendar_raw`
- `ods_reviews_raw`

### 清洗后明细表

- `dwd_listings`
- `dwd_calendar`
- `dwd_reviews`

### 应用结果表

- `ads_region_house_count`
- `ads_room_type_ratio`
- `ads_region_avg_price`
- `ads_price_review_relation`
- `ads_host_house_topn`
- `ads_monthly_review_trend`
- `ads_calendar_price_trend`
- `ads_review_word_freq`

## 9.2 重点分析指标

### 房源规模分析

- 各区域房源数量 TopN
- 各房型占比
- 各房东房源数量 TopN

### 价格分析

- 各区域平均价格
- 不同房型平均价格
- 价格分布区间
- 日历维度价格趋势

### 热度分析

- 各区域评论总数
- 房源评论数 TopN
- 月度评论趋势

### 地理分布分析

- 经纬度热力图
- 区域房源密度分布

### 文本分析

- 评论高频词统计
- 正向/负向高频词可作为扩展项

## 9.3 推荐对应图表

至少做 5 个，建议直接做 8 个，答辩更稳。

1. 柱状图：各区域房源数量
2. 饼图：房型占比
3. 折线图：月度评论趋势
4. 热力图：房源地理分布
5. 散点图：价格与评论数关系
6. 柱状图：Top10 房东房源数
7. 折线图：日历价格波动趋势
8. 词云图：评论高频词

## 10. Web 系统设计

## 10.1 架构模式

采用前后端分离：

- 前端：`Vue3 + Vite + Element Plus + ECharts`
- 后端：`Spring Boot + MyBatis + MySQL`

## 10.2 页面规划

建议页面不要做成单页堆图，而是拆成 5 个模块。

### 1. 登录页（可选）

课程项目可做简单固定账号登录，也可以省略，直接进入系统首页。

### 2. 首页 / 驾驶舱

展示关键指标卡片：

- 房源总数
- 房东总数
- 评论总数
- 平均价格
- 活跃区域数

并展示 2~3 个核心图表。

### 3. 房源分析页

展示：

- 区域房源数量柱状图
- 房型占比饼图
- 房东 TopN 柱状图

### 4. 价格分析页

展示：

- 各区域平均价格
- 价格区间分布
- 日历价格趋势折线图
- 价格与评论数散点图

### 5. 评论分析页

展示：

- 月度评论趋势
- 评论词云
- 热门评论房源排行

### 6. 地图分析页

展示：

- 基于经纬度的热力图
- 区域分布散点图

## 10.3 前端目录建议

```text
src/
├─ api/
├─ assets/
├─ components/
├─ layout/
├─ router/
├─ stores/
├─ views/
│  ├─ dashboard/
│  ├─ listing/
│  ├─ price/
│  ├─ review/
│  └─ map/
└─ utils/
```

## 10.4 后端目录建议

```text
src/main/java/com/airbnb/analysis
├─ controller
├─ service
├─ mapper
├─ entity
├─ dto
├─ config
└─ utils
```

## 11. 数据库设计

## 11.1 MySQL 作用

MySQL 不保存原始大规模数据，只保存：

- Hive 导出的聚合结果表
- 可选的系统用户表、日志表

## 11.2 推荐表设计

### 离线结果表

- `dashboard_overview`
- `region_house_count`
- `room_type_ratio`
- `region_avg_price`
- `price_review_relation`
- `host_house_topn`
- `monthly_review_trend`
- `calendar_price_trend`
- `review_word_freq`

## 12. 接口设计

## 12.1 接口原则

- 前端不直接查 Hive
- 所有展示数据通过 Spring Boot API 返回
- 返回格式统一

示例：

```json
{
  "code": 200,
  "message": "success",
  "data": []
}
```

## 12.2 推荐接口清单

- `GET /api/dashboard/overview`
- `GET /api/listing/region-count`
- `GET /api/listing/room-type-ratio`
- `GET /api/listing/host-topn`
- `GET /api/price/region-avg`
- `GET /api/price/trend`
- `GET /api/price/review-scatter`
- `GET /api/review/month-trend`
- `GET /api/review/wordcloud`
- `GET /api/map/heat`

## 13. 项目实施步骤

## 13.1 第一阶段：环境确认

目标：把基础运行环境核对清楚。

任务：

- 确认三台虚拟机 hostname、IP、SSH 互通正常
- 确认 HDFS 正常启动
- 确认 YARN 正常启动
- 确认 Hive 可连接
- 安装 MySQL
- 安装 Sqoop

交付：

- 环境截图
- 节点状态截图

## 13.2 第二阶段：数据接入

目标：由系统自动完成三个 CSV 文件的接收、校验和 HDFS 导入。

任务：

- 实现前端上传入口
- 实现 Spring Boot 上传接口与任务创建
- 编写自动化上传脚本
- 创建 HDFS 目录
- 由脚本自动上传原始数据
- 检查文件行数、大小、存储位置

交付：

- 上传页面截图
- HDFS 目录截图
- 上传脚本
- 任务状态截图

## 13.3 第三阶段：数据清洗

目标：在无需人工登录服务器执行命令的前提下，自动产出可用于 Hive 分析的干净数据。

任务：

- 编写 `listings` 清洗 MapReduce 程序
- 将清洗程序纳入总控脚本链路
- 输出清洗结果到 HDFS
- 对 `calendar`、`reviews` 做基础清洗
- 回传清洗阶段任务状态

交付：

- 清洗代码
- 清洗前后对比截图
- 异常值处理说明
- 清洗任务执行日志

## 13.4 第四阶段：Hive 建模与分析

目标：完成数仓表和分析结果表。

任务：

- 建立 ODS/DWD/ADS 表
- 编写统计 SQL
- 将结果落地为 Hive 结果表

交付：

- 建表 SQL
- 分析 SQL
- 查询结果截图

## 13.5 第五阶段：结果同步 MySQL

目标：由系统自动把前端需要的数据同步到应用库。

任务：

- 设计 MySQL 结果表
- 编写 Sqoop 导出脚本
- 将 Sqoop 导出纳入总控脚本链路
- 校验导出记录数

交付：

- Sqoop 脚本
- MySQL 表截图
- 导出阶段日志

## 13.6 第六阶段：后端开发

目标：完成接口层。

任务：

- 创建 Spring Boot 项目
- 实现文件上传接口
- 实现任务状态查询接口
- 实现异步任务调度服务
- 配置 MySQL
- 编写 Mapper / Service / Controller
- 联调接口返回结构

交付：

- 可访问 API
- Swagger 截图或接口测试截图
- 自动化任务接口测试结果

## 13.7 第七阶段：Vue3 前端开发

目标：完成可视化页面。

任务：

- 初始化 Vue3 项目
- 配置路由与页面布局
- 封装图表组件
- 对接后端接口

交付：

- 首页截图
- 各分析页面截图

## 13.8 第八阶段：测试与答辩材料整理

目标：保证项目能跑、能讲、能展示。

任务：

- 接口联调测试
- 页面功能测试
- 图表数据一致性校验
- 准备 PPT、流程图、架构图、截图

交付：

- 测试记录
- 演示脚本
- 答辩材料

## 14. 推荐项目目录结构

```text
airbnb-analysis/
├─ docs/
├─ data/
│  ├─ raw/
│  └─ clean/
├─ scripts/
│  ├─ upload_hdfs.sh
│  ├─ hive_init.sql
│  └─ sqoop_export.sh
├─ mapreduce/
├─ springboot-backend/
└─ vue3-frontend/
```

## 15. 时间安排建议

如果按 2 周推进，可参考下表：

### 第 1 周

- 第 1 天：环境确认、目录规划
- 第 2 天：数据上传 HDFS
- 第 3~4 天：MapReduce 清洗
- 第 5~6 天：Hive 建模与统计 SQL
- 第 7 天：Sqoop 导出 MySQL

### 第 2 周

- 第 8~9 天：Spring Boot 接口开发
- 第 10~11 天：Vue3 页面开发
- 第 12~13 天：联调与修复
- 第 14 天：整理报告、PPT、答辩截图

## 16. 风险与应对

### 风险 1：MapReduce 清洗开发量偏大

应对：

- 只对核心表 `listings.csv` 做完整 MapReduce 清洗
- 其余两表用 Hive SQL 辅助处理

### 风险 2：图表过多导致页面杂乱

应对：

- 按专题拆页
- 首页只放总览和核心指标

### 风险 3：Hive 与 MySQL 数据口径不一致

应对：

- 每张结果表记录生成时间
- 导出前先固定 SQL 统计逻辑

## 17. 最终交付清单

建议最终至少准备以下内容：

- 项目开发方案文档
- 原始数据文件
- HDFS 上传脚本
- MapReduce 清洗程序
- Hive 建表与分析 SQL
- Sqoop 导出脚本
- Spring Boot 后端项目
- Vue3 前端项目
- 演示截图
- 项目答辩 PPT

## 18. 建议答辩讲解顺序

1. 先讲项目背景和目标
2. 再讲整体架构
3. 演示 HDFS、Hive、MySQL 的数据流转
4. 演示 Vue3 可视化页面
5. 最后总结分析结论与项目亮点

## 19. 本方案的推荐落地版本

如果你的目标是“尽快做出可交付版本”，我建议采用下面这版，不要一开始做太重：

- 离线核心：`HDFS + MapReduce(仅 listings) + Hive + Sqoop + MySQL`
- Web 核心：`Spring Boot + Vue3 + ECharts`
- 图表数量：`8 个`

这是课程作业场景下实现成本、展示效果、答辩说服力之间比较平衡的一版。

## 20. 下一步建议

建议按下面顺序继续：

1. 先把这份方案定稿。
2. 再输出一份更具体的“任务拆解清单”。
3. 然后开始搭项目骨架：
   - Hive SQL
   - Spring Boot 后端
   - Vue3 前端
4. 最后补齐答辩材料。

如果后续继续推进，最合理的下一份文档应当是：

- 《数据库表结构设计》
- 《Hive 建表与分析 SQL 设计》
- 《Vue3 前端页面原型说明》
- 《Spring Boot 接口清单》
