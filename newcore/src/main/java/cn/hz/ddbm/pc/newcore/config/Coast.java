package cn.hz.ddbm.pc.newcore.config;

public class Coast {
    public static final String PLUGIN_EXECUTOR_SERVICE = "pluginExecutorService";
    public static final String ACTION_EXECUTOR_SERVICE = "actionExecutorService";

    public static class SAGA {
        public final static String EVENT_FORWARD = "forward";
        public final static String EVENT_BACKOFF = "backoff";

    }

    public static class FSM {
        public final static String EVENT_DEFAULT = "push";
    }
}
