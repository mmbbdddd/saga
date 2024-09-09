package cn.hz.ddbm.pc.newcore;

import java.util.Objects;

public enum FlowStatus {
    RUNNABLE(Type.runnable), PAUSE(Type.runnable), MANUAL(Type.runnable),
    SU(Type.end), FAIL(Type.end),
    ;

    private final Type type;

    FlowStatus(Type type) {
        this.type = type;
    }


    public static Boolean isPaused(FlowStatus status) {
        return Objects.equals(PAUSE, status);
    }

    public static Boolean isRunnable(FlowStatus status) {
        return Objects.equals(RUNNABLE, status);
    }

    public static Boolean isEnd(FlowStatus status) {
        return Objects.equals(status, SU) || Objects.equals(status, FAIL);
    }


    public enum Type {
        init, runnable, end
    }
}
