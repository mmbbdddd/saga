package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.State;

public class FsmState<S extends Enum<S>> extends State<S> {

    public FsmState(S state) {
        super(state);
    }

    @Override
    public S code() {
        return this.code;
    }
}
