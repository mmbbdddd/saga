package cn.hz.ddbm.pc.core.exception;

public class ActionResult<S extends Enum<S>> {
    Type      type;
    S         state;
    String    reason;
    Exception exception;

    public static <S extends Enum<S>> ActionResult<S> failover(S failover) {
        ActionResult<S> ar = new ActionResult<>();
        ar.type  = Type.EXCEPTION;
        ar.state = failover;
        return ar;
    }

    public static <S extends Enum<S>> ActionResult<S> result(S state) {
        ActionResult<S> ar = new ActionResult<>();
        ar.type  = Type.FINE;
        ar.state = state;
        return ar;
    }

    public static <S extends Enum<S>> ActionResult<S> fail(S state, String reason) {
        ActionResult<S> ar = new ActionResult<>();
        ar.type   = Type.ERROR;
        ar.state  = state;
        ar.reason = reason;
        return ar;
    }

    public static <S extends Enum<S>> ActionResult<S> exception(Exception e) {
        ActionResult<S> ar = new ActionResult<>();
        ar.exception = e;
        return ar;
    }

    enum Type {
        //不可预料的异常。业务结果不可知。可重试（IO错误，超时等）
        EXCEPTION,
        //可预料的异常，失败==》
        //                1，业务无法继续：参数错误
        //                2，业务无法继续：程序错误
        //                程序暂停
        ERROR,
        //业务结果可知，成功/失败==》下一个节点
        FINE;
    }
}
