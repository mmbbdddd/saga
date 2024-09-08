package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
public class FsmState<S extends Enum<S>> extends State<S> {
    S               state;
    @Setter
    FsmState.Offset offset;

    public FsmState(S state) {
        this.state = state;
    }

    @Override
    public S code() {
        return state;
    }

    public enum Offset {
        task, taskRetry, failover;
    }
}
