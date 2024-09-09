package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FsmState<?> fsmState = (FsmState<?>) o;
        return Objects.equals(state, fsmState.state) && offset == fsmState.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, offset);
    }

    public enum Offset {
        task, taskRetry, failover;
    }
}
