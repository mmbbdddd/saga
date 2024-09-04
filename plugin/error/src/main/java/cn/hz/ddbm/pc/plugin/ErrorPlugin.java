package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.newcore.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorPlugin<F extends FlowModel<S>, S extends State, W extends Worker<?>> extends Plugin<F, S, W> {


    @Override
    public String code() {
        return null;
    }

    @Override
    public void preAction(FlowContext<F, S, W> ctx) {

    }

    @Override
    public void postAction(S lastNode, FlowContext<F, S, W> ctx) {

    }

    @Override
    public void errorAction(S preNode, Exception e, FlowContext<F, S, W> ctx) {

    }

    @Override
    public void finallyAction(FlowContext<F, S, W> ctx) {

    }
}
