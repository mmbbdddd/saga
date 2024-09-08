package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.State;
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
    public S code() {
        return state;
    }

    public enum Offset {
        task, taskRetry, failover;
    }
}
