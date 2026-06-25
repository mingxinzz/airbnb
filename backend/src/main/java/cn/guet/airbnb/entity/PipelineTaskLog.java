package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pipeline_task_log")
public class PipelineTaskLog {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private String taskId;

    @TableField("log_time")
    private LocalDateTime logTime;

    @TableField("level")
    private String level;

    @TableField("stage")
    private String stage;

    @TableField("status")
    private String status;

    @TableField("message")
    private String message;
}
