package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.coast.Coasts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DigestLogPlugin<S extends Enum<S>> implements Plugin<S> {
    @Override
    public String code() {
        return Coasts.PLUGIN_DIGEST_LOG;
    }

    @Override
    public void preAction(String name, FsmContext<S, ?> ctx) {

    }

    @Override
    public void postAction(String name, S lastNode, FsmContext<S, ?> ctx) {
    }

    @Override
    public void onActionException(String name, S preNode, Exception e, FsmContext<S, ?> ctx) {
    }

    @Override
    public void onActionFinally(String name, FsmContext<S, ?> ctx) {

    }

    @Override
    public void postRoute(String routerName, S preNode, FsmContext<S, ?> ctx) {
        log.info("{},{},{},{}", ctx.getFlow()
                .getName(), ctx.getId(), preNode, ctx.getStatus());
    }

    @Override
    public void onRouteExcetion(String routerName, Exception e, FsmContext<S, ?> ctx) {

    }
}
