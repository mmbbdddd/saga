package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.coast.Coasts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorPlugin implements Plugin {
    @Override
    public String code() {
        return Coasts.PLUGIN_ERROR_LOG;
    }

    @Override
    public void onRouteExcetion(String routerName, Exception e, FsmContext ctx) {

    }

    @Override
    public void postRoute(String routerName, Enum preNode, FsmContext ctx) {

    }

    @Override
    public void onActionFinally(String name, FsmContext ctx) {

    }

    @Override
    public void onActionException(String actionName, Enum preNode, Exception e, FsmContext ctx) {

    }

    @Override
    public void postAction(String name, Enum lastNode, FsmContext ctx) {

    }

    @Override
    public void preAction(String name, FsmContext ctx) {

    }


//    @Override
//    public void onActionException(String actionName,S preNode, Exception e, FlowContext ctx) {
//        log.error("Action错误{}:", actionName, e);
//    }


}
