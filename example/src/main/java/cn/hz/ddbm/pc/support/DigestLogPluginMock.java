package cn.hz.ddbm.pc.support;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.log.Logs;
import org.springframework.stereotype.Component;

@Component("digestLogPlugin")
public class DigestLogPluginMock implements Plugin {
    @Override
    public String code() {
        return "digest";
    }

    @Override
    public void onRouteExcetion(String routerName, Exception e, FsmContext ctx) {

    }

    @Override
    public void onActionFinally(String name, FsmContext ctx) {

    }

    @Override
    public void onActionException(String actionName, Enum preNode, Exception e, FsmContext ctx) {

    }

    @Override
    public void postAction(String name, Enum lastNode, FsmContext ctx) {
        Logs.digest.info("{},{},{},{}", ctx.getFlow()
                .getName(), ctx.getId(), lastNode, ctx.getStatus()
                .getState());
    }

    @Override
    public void preAction(String name, FsmContext ctx) {

    }


    @Override
    public void postRoute(String routerName, Enum preNode, FsmContext ctx) {
        Logs.digest.info("{},{},{},{}", ctx.getFlow()
                .getName(), ctx.getId(), preNode, ctx.getStatus()
                .getState());
    }

}
