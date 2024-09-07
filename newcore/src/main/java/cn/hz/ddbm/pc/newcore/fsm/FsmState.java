package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;

@Getter
public class FsmState<S extends Enum<S>> extends State<Pair<S, FsmState.Offset>, FsmFlow<S>> {
    S      state;
    Offset offset;

    public FsmState(S state, Offset offset) {
        this.state  = state;
        this.offset = offset;
    }

    public static <S extends Enum<S>> FsmState<S> of(S state) {
        return new FsmState<>(state, Offset.task);
    }

    @Override
    public Pair<S, Offset> stateCode() {
        return Pair.of(state, offset);
    }

    @Override
    public boolean isEnd(FsmFlow<S> flow) {
        return false;
    }

    @Override
    public boolean isPause(FsmFlow<S> flow) {
        return false;
    }

    @Override
    public void setStatus(FlowStatus flowStatus) {

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
