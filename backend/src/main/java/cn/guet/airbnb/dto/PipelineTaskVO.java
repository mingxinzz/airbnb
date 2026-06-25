package cn.guet.airbnb.dto;

import lombok.Data;

@Data
public class PipelineTaskVO {

    private String taskId;

    private String taskStatus;

    private String currentStage;

    private String message;

    private String logFile;

    private String submittedAt;

    private String startTime;

    private String endTime;

    private String errorMessage;
}
