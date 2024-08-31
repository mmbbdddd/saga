package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorPlugin extends Plugin {


    @Override
    public String code() {
        return null;
    }

    @Override
    public void preAction(FlowContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FlowContext ctx) {

    }

    @Override
    public void errorAction(State preNode, Exception e, FlowContext ctx) {

    }

    @Override
    public void finallyAction(FlowContext ctx) {

    }
}
