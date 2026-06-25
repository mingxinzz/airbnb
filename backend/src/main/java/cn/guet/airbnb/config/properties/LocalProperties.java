package cn.guet.airbnb.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "airbnb.local")
public class LocalProperties {

    private String taskDir = "./data/tasks";
}
