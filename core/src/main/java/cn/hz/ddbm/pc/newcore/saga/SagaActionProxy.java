package cn.hz.ddbm.pc.newcore.saga;

public class SagaActionProxy {
    public SagaActionProxy(Class<? extends SagaAction> sagaActionClass) {

    }

    public void doSaga(SagaContext ctx) {

    }

    public SagaWorker.Offset querySaga(SagaContext ctx) {
        return null;
    }

    public void doSagaRollback(SagaContext ctx) {

    }

    public SagaWorker.Offset querySagaRollback(SagaContext ctx) {
        return null;
    }
}
