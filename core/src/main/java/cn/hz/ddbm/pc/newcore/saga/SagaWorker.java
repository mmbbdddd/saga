package cn.hz.ddbm.pc.newcore.saga;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;
import cn.hz.ddbm.pc.newcore.saga.workers.FailWorker;
import cn.hz.ddbm.pc.newcore.saga.workers.LocalSagaWorker;
import cn.hz.ddbm.pc.newcore.saga.workers.RemoteSagaWorker;
import cn.hz.ddbm.pc.newcore.saga.workers.SuWorker;

import static cn.hz.ddbm.pc.newcore.log.Logs.flow;

public abstract class SagaWorker {
    public Integer index;

    public SagaWorker(Integer index) {
        this.index = index;
    }

    public static SagaWorker of(Integer index, Class<? extends SagaAction> actionType) {
        if (LocalSagaAction.class.isAssignableFrom(actionType)) {
            return new LocalSagaWorker(index, actionType);
        } else {
            return new RemoteSagaWorker(index, actionType);
        }
    }

    public static SagaWorker suWorker(Integer index) {
        return new SuWorker(index);
    }

    public static SagaWorker failWorker() {
        return new FailWorker();
    }

    public abstract void execute(FlowContext<SagaState> ctx)  ;


    public enum Offset {
        task, task_failover, task_su, task_fail, rollback, rollback_failover, rollback_su, rollback_fail;
    }

}
