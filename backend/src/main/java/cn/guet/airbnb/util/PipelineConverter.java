package cn.guet.airbnb.util;

import cn.guet.airbnb.dto.PipelineTaskLogVO;
import cn.guet.airbnb.dto.PipelineTaskVO;
import cn.guet.airbnb.entity.PipelineTask;
import cn.guet.airbnb.entity.PipelineTaskLog;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class PipelineConverter {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private PipelineConverter() {
    }

    public static PipelineTaskVO toTaskVO(PipelineTask task) {
        PipelineTaskVO vo = new PipelineTaskVO();
        vo.setTaskId(task.getTaskId());
        vo.setTaskStatus(task.getTaskStatus());
        vo.setCurrentStage(task.getCurrentStage());
        vo.setMessage(task.getMessage());
        vo.setLogFile(task.getLogFile());
        vo.setSubmittedAt(format(task.getSubmittedAt()));
        vo.setStartTime(format(task.getStartTime()));
        vo.setEndTime(format(task.getEndTime()));
        vo.setErrorMessage(task.getErrorMessage());
        return vo;
    }

    public static PipelineTaskLogVO toLogVO(PipelineTaskLog log) {
        PipelineTaskLogVO vo = new PipelineTaskLogVO();
        vo.setLogTime(format(log.getLogTime()));
        vo.setLevel(log.getLevel());
        vo.setStage(log.getStage());
        vo.setStatus(log.getStatus());
        vo.setMessage(log.getMessage());
        return vo;
    }

    private static String format(LocalDateTime time) {
        return time == null ? null : time.format(FORMATTER);
    }
}
