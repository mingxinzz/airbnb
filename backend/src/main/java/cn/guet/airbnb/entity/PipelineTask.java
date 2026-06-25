package cn.guet.airbnb.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("pipeline_task")
public class PipelineTask {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("task_id")
    private String taskId;

    @TableField("task_status")
    private String taskStatus;

    @TableField("current_stage")
    private String currentStage;

    @TableField("message")
    private String message;

    @TableField("log_file")
    private String logFile;

    @TableField("local_dir")
    private String localDir;

    @TableField("submitted_at")
    private LocalDateTime submittedAt;

    @TableField("start_time")
    private LocalDateTime startTime;

    @TableField("end_time")
    private LocalDateTime endTime;

    @TableField("error_message")
    private String errorMessage;
}
