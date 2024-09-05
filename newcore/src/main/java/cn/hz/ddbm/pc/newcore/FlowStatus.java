package cn.hz.ddbm.pc.newcore;

import java.util.Objects;

public enum FlowStatus {
    INIT(Type.init),
    RUNNABLE(Type.runnable), PAUSE(Type.runnable),
    FINISH(Type.end), MANUAL(Type.end),
    ;

    private final Type type;

    FlowStatus(Type type) {
        this.type = type;
    }

    public static Boolean isInit(FlowStatus status) {
        return Objects.equals(Type.init, status.type);
    }

    public static Boolean isRunnable(FlowStatus status) {
        return !Objects.equals(Type.end, status.type);
    }

    public static Boolean isEnd(FlowStatus status) {
        return Objects.equals(Type.end, status.type);
    }
    public static Boolean isPause(FlowStatus status) {
        return Objects.equals(PAUSE, status);
    }

    public enum Type {
        init, runnable, end
    }
}
