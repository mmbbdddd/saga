package cn.hz.ddbm.pc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "dddd.pc")
@Data
public class PcProperties {
    DefineStyle    defineStyle    = DefineStyle.dsl;
    StatusManager  statusManager  = new StatusManager();
    SessionManager sessionManager = new SessionManager();
    Statistics     statistics     = new Statistics();

    public enum DefineStyle {
        dsl, json, xml
    }

    @Data
    public static class StatusManager {
        Integer cacheSize = 256;
        Integer hours     = 2;
    }

    @Data
    public static class Statistics {
        Integer cacheSize = 256;
        Integer hours     = 2;
    }

    @Data
    public static class SessionManager {
        Integer cacheSize = 256;
        Integer hours     = 2;
    }
}
