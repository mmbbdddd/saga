package cn.hz.ddbm.pc.newcore.plugins;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.log.Logs;

public class ErrorPlugin extends Plugin {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void preAction(FlowContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FlowContext ctx) {

    }

    @Override
    public void errorAction(State preNode, Exception e, FlowContext ctx) {
        Logs.error.error("", e);

    }

    @Override
    public void finallyAction(State preNode,FlowContext ctx) {
    }


}
