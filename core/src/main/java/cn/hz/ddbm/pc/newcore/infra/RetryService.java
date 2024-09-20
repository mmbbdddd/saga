package cn.hz.ddbm.pc.newcore.infra;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;

public interface RetryService {
    <S extends Enum<S>> void addTask(FlowContext<FsmState > ctx);
}
