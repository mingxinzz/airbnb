package cn.guet.airbnb.service.pipeline;

import cn.guet.airbnb.config.properties.LocalProperties;
import cn.guet.airbnb.config.properties.RemoteProperties;
import cn.guet.airbnb.dto.PipelineTaskLogVO;
import cn.guet.airbnb.entity.PipelineTask;
import cn.guet.airbnb.service.remote.SshRemoteService;
import cn.guet.airbnb.util.PipelineLogFileParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class PipelineLogReader {

    private final SshRemoteService sshRemoteService;
    private final RemoteProperties remoteProperties;
    private final LocalProperties localProperties;

    public List<PipelineTaskLogVO> readLogs(PipelineTask task) {
        String logFile = task.getLogFile();
        if (!StringUtils.hasText(logFile)) {
            return List.of();
        }

        String content = readLogContent(logFile, task);
        return PipelineLogFileParser.parse(content);
    }

    private String readLogContent(String logFile, PipelineTask task) {
        if (remoteProperties.isEnabled() && logFile.startsWith("/")) {
            try {
                return sshRemoteService.readRemoteFile(logFile);
            } catch (Exception e) {
                log.warn("读取远程日志失败, taskId={}, logFile={}, error={}",
                        task.getTaskId(), logFile, e.getMessage());
                return readLocalFallback(task);
            }
        }

        Path localPath = Paths.get(logFile);
        if (Files.exists(localPath)) {
            return readLocalFile(localPath);
        }

        return readLocalFallback(task);
    }

    private String readLocalFallback(PipelineTask task) {
        Path fallback = localProperties.getTaskDirPath()
                .resolve(task.getTaskId())
                .resolve("logs")
                .resolve(task.getTaskId() + ".log");
        if (Files.exists(fallback)) {
            return readLocalFile(fallback);
        }
        return "";
    }

    private String readLocalFile(Path path) {
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.warn("读取本地日志失败, path={}, error={}", path, e.getMessage());
            return "";
        }
    }
}
