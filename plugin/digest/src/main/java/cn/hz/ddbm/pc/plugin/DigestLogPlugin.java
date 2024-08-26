package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.coast.Coasts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DigestLogPlugin implements Plugin {
    @Override
    public String code() {
        return Coasts.PLUGIN_DIGEST_LOG;
    }

    @Override
    public void interrupteFlow(String s, FsmContext ctx) {

    }

    @Override
    public void onActionFinally(String name, FsmContext ctx) {

    }

    @Override
    public void onActionException(String actionName, Enum preNode, Exception e, FsmContext ctx) {

    }

    @Override
    public void postAction(String name, Enum lastNode, FsmContext ctx) {
        log.info("{},{},{},{}", ctx.getFlow()
                .getName(), ctx.getId(), lastNode, ctx.getStatus());
    }

    @Override
    public void preAction(String name, FsmContext ctx) {

    }


}
