package cn.hz.ddbm.pc.core.enums;

public enum FlowStatus {
    INIT, RUNNABLE, PAUSE, CANCEL, FINISH;

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
