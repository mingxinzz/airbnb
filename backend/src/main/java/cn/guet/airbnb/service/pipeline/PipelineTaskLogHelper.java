package cn.guet.airbnb.service.pipeline;

import cn.guet.airbnb.constant.PipelineConstants;
import cn.guet.airbnb.entity.PipelineTaskLog;
import cn.guet.airbnb.mapper.PipelineTaskLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class PipelineTaskLogHelper {

    private final PipelineTaskLogMapper pipelineTaskLogMapper;

    public void addLog(String taskId, String level, String stage, String status, String message) {
        PipelineTaskLog log = new PipelineTaskLog();
        log.setTaskId(taskId);
        log.setLogTime(LocalDateTime.now());
        log.setLevel(level);
        log.setStage(stage);
        log.setStatus(status);
        log.setMessage(message);
        pipelineTaskLogMapper.insert(log);
    }
}
