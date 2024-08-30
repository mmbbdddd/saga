package cn.hz.ddbm.pc.support;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.log.Logs;
import org.springframework.stereotype.Component;

@Component("digestLogPlugin")
public class DigestLogPluginMock implements Plugin {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void preAction(FsmContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FsmContext ctx) {
        Logs.digest.info("{},{},{},{}", ctx.getFlow()
                .getName(), ctx.getId(), lastNode, ctx.getState());
    }

    @Override
    public void onActionException(State preNode, Exception e, FsmContext ctx) {

    }

    @Override
    public void onActionFinally(FsmContext ctx) {

    }




}
