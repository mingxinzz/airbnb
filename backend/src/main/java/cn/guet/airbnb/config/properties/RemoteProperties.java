package cn.guet.airbnb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "airbnb.remote")
public class RemoteProperties {

    private boolean enabled = true;

    private String host;

    private int port = 22;

    private String username;

    private String password;

    private int connectTimeoutMs = 10000;

    private int sessionTimeoutMs = 30000;

    private String taskBaseDir;

    private String runScript;

    private String logDir;
}
