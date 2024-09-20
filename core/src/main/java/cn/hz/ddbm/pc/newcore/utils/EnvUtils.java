package cn.hz.ddbm.pc.newcore.utils;

import java.util.Objects;

public class EnvUtils {
    public static final String RUN_MODE       = "run_mode";

    public static Boolean isDev() {
        return false;
    }

    public static Boolean isChaos() {
        String runMode = System.getProperty(RUN_MODE);
        return Objects.equals(runMode, "true");
    }

    public static void setChaosMode(Boolean isChaosMode) {
        System.setProperty(RUN_MODE, isChaosMode.toString());
    }
}
