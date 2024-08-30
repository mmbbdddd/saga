package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;

import java.io.Serializable;

public class FsmState<S extends Serializable> extends State {
    @Getter
    S state;
    public FsmState(S state) {
        super(state);
        this.state = state;
    }
}
