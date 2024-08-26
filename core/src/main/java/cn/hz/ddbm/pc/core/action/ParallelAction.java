package cn.hz.ddbm.pc.core.action;

public interface ParallelAction<S extends Enum<S>> extends SagaAction<S> {
    ActionResultType resultType(S state);
    ActionResultType resultType(Exception e);

    enum ActionResultType {
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
