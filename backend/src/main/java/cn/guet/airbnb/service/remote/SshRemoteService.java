package cn.guet.airbnb.service.remote;

import cn.guet.airbnb.common.BusinessException;
import cn.guet.airbnb.config.properties.RemoteProperties;
import cn.guet.airbnb.constant.PipelineConstants;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class SshRemoteService {

    private final RemoteProperties remoteProperties;

    public void ensureEnabled() {
        if (!remoteProperties.isEnabled()) {
            throw new BusinessException(500, "远程执行未启用，请检查 airbnb.remote.enabled 配置");
        }
    }

    public void mkdirRemoteTaskDirs(String taskId) {
        ensureEnabled();
        String inputDir = remoteInputDir(taskId);
        String logsDir = remoteTaskDir(taskId) + "/logs";
        execCommand("mkdir -p " + inputDir + " " + logsDir);
    }

    public void uploadMultipartFiles(MultipartFile listingsFile,
                                     MultipartFile calendarFile,
                                     MultipartFile reviewsFile,
                                     String taskId) {
        ensureEnabled();
        mkdirRemoteTaskDirs(taskId);
        try {
            uploadStream(listingsFile.getInputStream(), remoteInputDir(taskId) + "/" + PipelineConstants.LISTINGS_FILE);
            uploadStream(calendarFile.getInputStream(), remoteInputDir(taskId) + "/" + PipelineConstants.CALENDAR_FILE);
            uploadStream(reviewsFile.getInputStream(), remoteInputDir(taskId) + "/" + PipelineConstants.REVIEWS_FILE);
        } catch (IOException e) {
            throw new BusinessException(500, "读取上传文件失败: " + e.getMessage());
        }
    }

    public void uploadLocalInputDir(Path localInputDir, String taskId) {
        ensureEnabled();
        mkdirRemoteTaskDirs(taskId);
        uploadPath(localInputDir.resolve(PipelineConstants.LISTINGS_FILE), remoteInputDir(taskId) + "/" + PipelineConstants.LISTINGS_FILE);
        uploadPath(localInputDir.resolve(PipelineConstants.CALENDAR_FILE), remoteInputDir(taskId) + "/" + PipelineConstants.CALENDAR_FILE);
        uploadPath(localInputDir.resolve(PipelineConstants.REVIEWS_FILE), remoteInputDir(taskId) + "/" + PipelineConstants.REVIEWS_FILE);
    }

    public void startPipelineScript(String taskId) {
        ensureEnabled();
        String logFile = buildLogFilePath(taskId);
        String command = String.format("nohup bash %s %s >> %s 2>&1 &",
                remoteProperties.getRunScript(), taskId, logFile);
        log.info("远程启动 Pipeline 脚本, taskId={}, command={}", taskId, command);
        execCommand(command);
    }

    public String buildLogFilePath(String taskId) {
        return remoteProperties.getLogDir() + "/" + taskId + ".log";
    }

    public String remoteInputDir(String taskId) {
        return remoteTaskDir(taskId) + "/input";
    }

    private String remoteTaskDir(String taskId) {
        return remoteProperties.getTaskBaseDir() + "/" + taskId;
    }

    private void uploadPath(Path localPath, String remotePath) {
        try (InputStream inputStream = Files.newInputStream(localPath)) {
            uploadStream(inputStream, remotePath);
        } catch (IOException e) {
            throw new BusinessException(500, "读取本地文件失败: " + localPath.getFileName());
        }
    }

    private void uploadStream(InputStream inputStream, String remotePath) {
        Session session = null;
        ChannelSftp sftp = null;
        try {
            session = openSession();
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(remoteProperties.getSessionTimeoutMs());
            sftp.put(inputStream, remotePath);
        } catch (JSchException | SftpException e) {
            throw new BusinessException(500, "SFTP 上传失败: " + remotePath + ", " + e.getMessage());
        } finally {
            disconnect(sftp, session);
        }
    }

    private void execCommand(String command) {
        Session session = null;
        ChannelExec channel = null;
        try {
            session = openSession();
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);
            channel.connect(remoteProperties.getSessionTimeoutMs());

            try (InputStream errorStream = channel.getErrStream();
                 InputStream inputStream = channel.getInputStream()) {
                readStream(inputStream);
                readStream(errorStream);
            }

            while (!channel.isClosed()) {
                Thread.sleep(100);
            }

            if (channel.getExitStatus() != 0) {
                throw new BusinessException(500, "远程命令执行失败: " + command);
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(500, "SSH 执行异常: " + e.getMessage());
        } finally {
            disconnect(channel, session);
        }
    }

    private Session openSession() throws JSchException {
        JSch jsch = new JSch();
        Session session = jsch.getSession(remoteProperties.getUsername(),
                remoteProperties.getHost(), remoteProperties.getPort());
        session.setPassword(remoteProperties.getPassword());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(remoteProperties.getConnectTimeoutMs());
        return session;
    }

    private void readStream(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return;
        }
        byte[] buffer = new byte[1024];
        while (inputStream.available() > 0) {
            int read = inputStream.read(buffer, 0, buffer.length);
            if (read < 0) {
                break;
            }
            log.debug("SSH output: {}", new String(buffer, 0, read, StandardCharsets.UTF_8));
        }
    }

    private void disconnect(com.jcraft.jsch.Channel channel, Session session) {
        if (channel != null && channel.isConnected()) {
            channel.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }
}
