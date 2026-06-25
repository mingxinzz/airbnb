package cn.guet.airbnb.dto;

import lombok.Data;

@Data
public class PipelineTaskLogVO {

    private String logTime;

    private String level;

    private String stage;

    private String status;

    private String message;
}
