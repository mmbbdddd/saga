package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

public interface ChaosHandler {

    void locker();

    void session();

    void statistics();

    void status();
}
