package cn.guet.airbnb.service.pipeline;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.constant.PipelineConstants;
import cn.guet.airbnb.entity.PipelineTask;
import cn.guet.airbnb.mapper.PipelineTaskMapper;
import cn.guet.airbnb.service.remote.SshRemoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class PipelineAsyncExecutor {

    private final SshRemoteService sshRemoteService;
    private final PipelineTaskMapper pipelineTaskMapper;

    @Async("pipelineExecutor")
    public void startRemotePipeline(String taskId) {
        try {
            sshRemoteService.startPipelineScript(taskId);
        } catch (Exception e) {
            log.error("远程启动 Pipeline 失败, taskId={}", taskId, e);
            PipelineTask task = getTask(taskId);
            task.setTaskStatus(PipelineConstants.TaskStatus.FAILED);
            task.setCurrentStage(PipelineConstants.TaskStage.FAILED);
            task.setMessage("远程启动分析脚本失败");
            task.setErrorMessage(e.getMessage());
            task.setEndTime(LocalDateTime.now());
            pipelineTaskMapper.updateById(task);
        }
    }

    private PipelineTask getTask(String taskId) {
        PipelineTask task = pipelineTaskMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PipelineTask>()
                        .eq(PipelineTask::getTaskId, taskId)
                        .last("LIMIT 1")
        ).stream().findFirst().orElse(null);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        return task;
    }
}
