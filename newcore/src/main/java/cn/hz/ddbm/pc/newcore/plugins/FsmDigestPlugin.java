package cn.hz.ddbm.pc.newcore.plugins;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.io.Serializable;

public class FsmDigestPlugin implements Plugin {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void preAction(FlowContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FlowContext ctx) {
        String       flow         = ctx.getFlow().getName();
        Serializable id           = ctx.getId();
        String       from         = lastNode.code();
        String       event        = ((FsmContext) ctx).getEvent();
        String       action       = ctx.getAction().code();
        String       actionResult = ctx.getActionResult().toString();
        String       targetStatus = ctx.getState().code();

        Logs.digest.info("{},{},{},{},{},{},{}==>{}", flow,id,from,event,action,actionResult,from,targetStatus);
    }

    @Override
    public void errorAction(State preNode, Exception e, FlowContext ctx) {
        Logs.digest.info("{},{}:", ctx.getFlow()
                .getName(), ctx.getId(), e);
    }

    @Override
    public void finallyAction(FlowContext ctx) {
//        log.info("{},{},{},{}", ctx.getFlow().getName(), ctx.getId(),  ctx.getAction().code(), ctx.getActionResult());
//        log.info("{},{},{},{}", ctx.getFlow().getName(), ctx.getId(),  ctx.getStatus(), ctx.getState());
    }


}
