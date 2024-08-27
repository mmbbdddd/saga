package cn.hz.ddbm.pc.core.enums;

import java.util.Objects;

public enum FlowStatus {
    INIT, RUNNABLE, PAUSE, CANCEL, FINISH;

    public static Boolean isRunnable(FlowStatus status) {
        return Objects.equals(INIT, status) || Objects.equals(RUNNABLE, status) || Objects.equals(PAUSE, status) ;
    }
    public static Boolean isEnd(FlowStatus status) {
        return Objects.equals(FINISH, status) || Objects.equals(CANCEL, status) ;
    }

    public FlowStatus on(Event event) {
        switch (this) {
            case INIT: {
                switch (event) {
                    case PUSH:
                        return RUNNABLE;
                    case PAUSE:
                        return PAUSE;
                    case CANCEL:
                        return CANCEL;
                }
                break;
            }
            case RUNNABLE:
                switch (event) {
                    case ROLLBACK:
                        return INIT;
                }
                break;
            case PAUSE:
                switch (event) {
                    case WAKEUP:
                        return RUNNABLE;
                }
                break;
            case CANCEL:
                break;

            case FINISH:
                break;

        }
        return this;
    }

    public enum Event {
        PUSH, ROLLBACK, PAUSE, CANCEL, WAKEUP;

        public static boolean isFlowEvent(String event) {
            try {
                Event.valueOf(event.toUpperCase());
                return true;
            } catch (Exception e) {
                return false;
            }
        }
    }
}
