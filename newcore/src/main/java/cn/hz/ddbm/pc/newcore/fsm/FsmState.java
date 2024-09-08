package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FsmState<S extends Enum<S>> extends State<S> {
    S state;
    @Setter
    FsmState.Offset offset;


    public FsmState(S state, FsmState.Offset offset) {
        this.state  = state;
        this.offset = offset;
    }

    @Override
    public Triple<FlowStatus, S, FsmState.Offset> code() {
        return Triple.of(status, state, offset);
    }

    public enum Offset {
        task, taskRetry, failover;
    }
}
