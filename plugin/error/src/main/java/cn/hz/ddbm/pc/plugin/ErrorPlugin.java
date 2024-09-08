package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.newcore.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorPlugin<S extends State> extends Plugin<S> {


    @Override
    public String code() {
        return null;
    }

    @Override
    public void preAction(FlowContext<?, S, ?> ctx) {

    }

    @Override
    public void postAction(S preState, FlowContext<?, S, ?> ctx) {

    }

    @Override
    public void errorAction(S preState, Exception e, FlowContext<?, S, ?> ctx) {

    }

    @Override
    public void finallyAction(FlowContext<?, S, ?> ctx) {

    }

}
