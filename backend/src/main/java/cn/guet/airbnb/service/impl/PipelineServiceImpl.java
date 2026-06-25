package cn.guet.airbnb.service.impl;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.config.properties.LocalProperties;
import cn.guet.airbnb.constant.PipelineConstants;
import cn.guet.airbnb.dto.PipelineTaskLogVO;
import cn.guet.airbnb.dto.PipelineTaskVO;
import cn.guet.airbnb.entity.PipelineTask;
import cn.guet.airbnb.entity.PipelineTaskLog;
import cn.guet.airbnb.mapper.PipelineTaskLogMapper;
import cn.guet.airbnb.mapper.PipelineTaskMapper;
import cn.guet.airbnb.service.PipelineService;
import cn.guet.airbnb.service.pipeline.PipelineAsyncExecutor;
import cn.guet.airbnb.service.pipeline.PipelineTaskLogHelper;
import cn.guet.airbnb.service.remote.SshRemoteService;
import cn.guet.airbnb.util.PipelineConverter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PipelineServiceImpl implements PipelineService {

    private final PipelineTaskMapper pipelineTaskMapper;
    private final PipelineTaskLogMapper pipelineTaskLogMapper;
    private final LocalProperties localProperties;
    private final SshRemoteService sshRemoteService;
    private final PipelineAsyncExecutor pipelineAsyncExecutor;
    private final PipelineTaskLogHelper pipelineTaskLogHelper;

    @Override
    public PipelineTaskVO upload(MultipartFile listingsFile, MultipartFile calendarFile, MultipartFile reviewsFile) {
        validateUploadFiles(listingsFile, calendarFile, reviewsFile);

        String taskId = generateTaskId();
        String localDir = saveFilesLocally(taskId, listingsFile, calendarFile, reviewsFile);
        LocalDateTime now = LocalDateTime.now();

        PipelineTask task = new PipelineTask();
        task.setTaskId(taskId);
        task.setTaskStatus(PipelineConstants.TaskStatus.CREATED);
        task.setCurrentStage(PipelineConstants.TaskStage.CREATED);
        task.setMessage("文件上传完成，任务已创建");
        task.setLogFile(sshRemoteService.buildLogFilePath(taskId));
        task.setLocalDir(localDir);
        task.setSubmittedAt(now);
        pipelineTaskMapper.insert(task);

        pipelineTaskLogHelper.addLog(taskId,
                PipelineConstants.LogLevel.INFO,
                PipelineConstants.TaskStage.CREATED,
                PipelineConstants.TaskStatus.CREATED,
                "文件已保存到本地，等待启动处理");

        return PipelineConverter.toTaskVO(task);
    }

    @Override
    public PipelineTaskVO start(String taskId) {
        PipelineTask task = getRequiredTask(taskId);
        if (!PipelineConstants.TaskStatus.CREATED.equals(task.getTaskStatus())) {
            throw new BusinessException(400, "当前任务状态不允许启动: " + task.getTaskStatus());
        }
        if (!StringUtils.hasText(task.getLocalDir())) {
            throw new BusinessException(400, "任务本地文件目录不存在");
        }

        Path localInputDir = Paths.get(task.getLocalDir(), "input");
        if (!Files.exists(localInputDir.resolve(PipelineConstants.LISTINGS_FILE))
                || !Files.exists(localInputDir.resolve(PipelineConstants.CALENDAR_FILE))
                || !Files.exists(localInputDir.resolve(PipelineConstants.REVIEWS_FILE))) {
            throw new BusinessException(400, "任务文件不完整，请重新上传");
        }

        try {
            sshRemoteService.uploadLocalInputDir(localInputDir, taskId);
        } catch (BusinessException e) {
            markTaskFailed(task, "上传文件到远程服务器失败", e.getMessage());
            throw e;
        }

        LocalDateTime now = LocalDateTime.now();
        task.setTaskStatus(PipelineConstants.TaskStatus.RUNNING);
        task.setCurrentStage(PipelineConstants.TaskStage.UPLOAD_RAW_TO_HDFS);
        task.setMessage("开始上传原始文件到 HDFS");
        task.setStartTime(now);
        task.setLogFile(sshRemoteService.buildLogFilePath(taskId));
        pipelineTaskMapper.updateById(task);

        pipelineTaskLogHelper.addLog(taskId,
                PipelineConstants.LogLevel.INFO,
                PipelineConstants.TaskStage.UPLOAD_RAW_TO_HDFS,
                PipelineConstants.TaskStatus.RUNNING,
                "文件已上传到远程服务器，准备启动分析脚本");

        pipelineAsyncExecutor.startRemotePipeline(taskId);
        return PipelineConverter.toTaskVO(task);
    }

    @Override
    public PipelineTaskVO getStatus(String taskId) {
        return PipelineConverter.toTaskVO(getRequiredTask(taskId));
    }

    @Override
    public List<PipelineTaskLogVO> getLogs(String taskId) {
        getRequiredTask(taskId);
        List<PipelineTaskLog> logs = pipelineTaskLogMapper.selectList(
                new LambdaQueryWrapper<PipelineTaskLog>()
                        .eq(PipelineTaskLog::getTaskId, taskId)
                        .orderByAsc(PipelineTaskLog::getLogTime)
                        .orderByAsc(PipelineTaskLog::getId)
        );
        return logs.stream().map(PipelineConverter::toLogVO).collect(Collectors.toList());
    }

    @Override
    public PipelineTaskVO uploadAndStart(MultipartFile listingsFile, MultipartFile calendarFile, MultipartFile reviewsFile) {
        validateUploadFiles(listingsFile, calendarFile, reviewsFile);
        sshRemoteService.ensureEnabled();

        String taskId = generateTaskId();
        LocalDateTime now = LocalDateTime.now();
        String localDir = saveFilesLocally(taskId, listingsFile, calendarFile, reviewsFile);

        try {
            sshRemoteService.uploadLocalInputDir(Paths.get(localDir, "input"), taskId);
        } catch (BusinessException e) {
            markTaskFailed(createFailedTask(taskId, localDir, now), "上传文件到远程服务器失败", e.getMessage());
            throw e;
        }

        PipelineTask task = new PipelineTask();
        task.setTaskId(taskId);
        task.setTaskStatus(PipelineConstants.TaskStatus.RUNNING);
        task.setCurrentStage(PipelineConstants.TaskStage.UPLOAD_RAW_TO_HDFS);
        task.setMessage("开始上传原始文件到HDFS");
        task.setLogFile(sshRemoteService.buildLogFilePath(taskId));
        task.setLocalDir(localDir);
        task.setSubmittedAt(now);
        task.setStartTime(now);
        pipelineTaskMapper.insert(task);

        pipelineTaskLogHelper.addLog(taskId,
                PipelineConstants.LogLevel.INFO,
                PipelineConstants.TaskStage.UPLOAD_RAW_TO_HDFS,
                PipelineConstants.TaskStatus.RUNNING,
                "文件已上传到远程服务器");

        pipelineAsyncExecutor.startRemotePipeline(taskId);
        return PipelineConverter.toTaskVO(task);
    }

    private void validateUploadFiles(MultipartFile listingsFile,
                                       MultipartFile calendarFile,
                                       MultipartFile reviewsFile) {
        validateFile(listingsFile, PipelineConstants.LISTINGS_FILE);
        validateFile(calendarFile, PipelineConstants.CALENDAR_FILE);
        validateFile(reviewsFile, PipelineConstants.REVIEWS_FILE);
    }

    private void validateFile(MultipartFile file, String expectedName) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, expectedName + " 不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        if (!StringUtils.hasText(originalFilename)) {
            throw new BusinessException(400, expectedName + " 文件名无效");
        }
        String filename = Paths.get(originalFilename).getFileName().toString();
        if (!expectedName.equalsIgnoreCase(filename)) {
            throw new BusinessException(400, "文件名必须为 " + expectedName);
        }
    }

    private String saveFilesLocally(String taskId,
                                    MultipartFile listingsFile,
                                    MultipartFile calendarFile,
                                    MultipartFile reviewsFile) {
        Path taskRoot = Paths.get(localProperties.getTaskDir(), taskId);
        Path inputDir = taskRoot.resolve("input");
        try {
            Files.createDirectories(inputDir);
            listingsFile.transferTo(inputDir.resolve(PipelineConstants.LISTINGS_FILE).toFile());
            calendarFile.transferTo(inputDir.resolve(PipelineConstants.CALENDAR_FILE).toFile());
            reviewsFile.transferTo(inputDir.resolve(PipelineConstants.REVIEWS_FILE).toFile());
            return taskRoot.toAbsolutePath().toString();
        } catch (IOException e) {
            throw new BusinessException(500, "保存本地任务文件失败: " + e.getMessage());
        }
    }

    private PipelineTask getRequiredTask(String taskId) {
        PipelineTask task = pipelineTaskMapper.selectOne(
                new LambdaQueryWrapper<PipelineTask>().eq(PipelineTask::getTaskId, taskId)
        );
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        return task;
    }

    private void markTaskFailed(PipelineTask task, String message, String errorMessage) {
        task.setTaskStatus(PipelineConstants.TaskStatus.FAILED);
        task.setCurrentStage(PipelineConstants.TaskStage.FAILED);
        task.setMessage(message);
        task.setErrorMessage(errorMessage);
        task.setEndTime(LocalDateTime.now());
        pipelineTaskMapper.updateById(task);
        pipelineTaskLogHelper.addLog(task.getTaskId(),
                PipelineConstants.LogLevel.ERROR,
                PipelineConstants.TaskStage.FAILED,
                PipelineConstants.TaskStatus.FAILED,
                message + ": " + errorMessage);
    }

    private String generateTaskId() {
        return "TASK" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%04d", ThreadLocalRandom.current().nextInt(10000));
    }

    private PipelineTask createFailedTask(String taskId, String localDir, LocalDateTime submittedAt) {
        PipelineTask task = new PipelineTask();
        task.setTaskId(taskId);
        task.setLocalDir(localDir);
        task.setSubmittedAt(submittedAt);
        task.setLogFile(sshRemoteService.buildLogFilePath(taskId));
        pipelineTaskMapper.insert(task);
        return task;
    }
}
