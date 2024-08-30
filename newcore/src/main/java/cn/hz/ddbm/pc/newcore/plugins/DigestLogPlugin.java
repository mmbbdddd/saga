package cn.hz.ddbm.pc.newcore.plugins;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.saga.SagaState;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DigestLogPlugin implements Plugin {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void preAction(FlowContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FlowContext ctx) {
        Boolean forward   = ((SagaState) ctx.getState()).getIsForward();
        String  directStr = forward ? ">>>>" : "<<<<";
        log.info("{},{},{},{},{}=>{}", directStr, ctx.getFlow().getName(), ctx.getId(), ctx.getStatus(), lastNode, ctx.getState());
    }

    @Override
    public void errorAction(State preNode, Exception e, FlowContext ctx) {
        log.info("{},{}:", ctx.getFlow()
                .getName(), ctx.getId(), e);
    }

    @Override
    public void finallyAction(FlowContext ctx) {
//        log.info("{},{},{},{}", ctx.getFlow().getName(), ctx.getId(),  ctx.getAction().code(), ctx.getActionResult());
//        log.info("{},{},{},{}", ctx.getFlow().getName(), ctx.getId(),  ctx.getStatus(), ctx.getState());
    }


}
