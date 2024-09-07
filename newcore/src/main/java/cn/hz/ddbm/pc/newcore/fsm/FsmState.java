package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;

@Getter
public class FsmState<S extends Enum<S>> extends State<Triple<FlowStatus, S, FsmState.Offset>> {
    S      state;
    Offset offset;

    public FsmState(FlowStatus status, S state, Offset offset) {
        super(status);
        this.state  = state;
        this.offset = offset;
    }

    public static <S extends Enum<S>> FsmState<S> of(S state) {
        return new FsmState<>(FlowStatus.RUNNABLE, state, Offset.task);
    }

    @Override
    public Triple<FlowStatus, S, Offset> code() {
        return Triple.of(status, state, offset);
    }


    @Override
    public String toString() {
        return state + "." + offset;
    }

    public FsmState<S> offset(Offset offset) {
        return null;
    }

    public enum Offset {
        task, failover
    }

}
