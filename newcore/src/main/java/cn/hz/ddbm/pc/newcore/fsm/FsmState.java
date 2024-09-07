package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;

@Getter
public class FsmState<S extends Enum<S>> extends State<S, FsmFlow<S>> {

    public FsmState(S state, OffsetState offset) {
        this.state  = state;
        this.offset = offset;
    }

    public static <S extends Enum<S>> FsmState<S> of(S state) {
        return new FsmState<>(state, OffsetState.task);
    }

    @Override
    public Pair<S, OffsetState> stateCode() {
        return Pair.of(state, offset);
    }

    @Override
    public boolean isEnd(FsmFlow<S> flow) {
        return flow.isEnd(this);
    }


    @Override
    public String toString() {
        return state + "." + offset;
    }

    public FsmState<S> offset(OffsetState offset) {
        return null;
    }



}
