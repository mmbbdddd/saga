package cn.hz.ddbm.pc.newcore.utils;

import java.util.Objects;

public class EnvUtils {
    public static final String RUN_MODE       = "run_mode";
    public static final String RUN_MODE_CHAOS = "chaos";

    public static Boolean isDev() {
        return false;
    }

    public static Boolean isChaos() {
        String runMode = System.getProperty(RUN_MODE);
        return Objects.equals(runMode, RUN_MODE_CHAOS);
    }

    public static void setRunModeChaos() {
        System.setProperty(RUN_MODE, RUN_MODE_CHAOS);
    }
}
