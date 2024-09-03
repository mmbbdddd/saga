package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.State;

public class FsmState<S extends Enum<S>> extends State<Pair<S, FsmState.Offset>> {
    S      state;
    Offset offset;

    public FsmState(S state, Offset offset) {
        super(Pair.of(state, Offset.task));
        this.state  = state;
        this.offset = offset;
    }

    public static <S extends Enum<S>> FsmState<S> of(S state) {
        return new FsmState<>(state, Offset.task);
    }

    @Override
    public Pair<S, Offset> code() {
        return this.code;
    }

    public enum Offset {
        task, failover
    }
}
