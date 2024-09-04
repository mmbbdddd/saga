package cn.hz.ddbm.pc.newcore.chaos;


import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface ChaosHandler {

    void locker() throws RuntimeException;

    void session() throws RuntimeException;

    void statistics() throws RuntimeException;

    void status() throws RuntimeException;

    <S extends Enum<S>> S handleRouter(FsmContext<S> ctx);

    Object executeQuery(FsmContext<?> ctx);

    Boolean executeQuery(SagaContext<?> ctx);

    Boolean rollbackQuery(SagaContext<?> ctx);
}
