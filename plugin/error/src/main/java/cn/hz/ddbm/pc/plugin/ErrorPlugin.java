package cn.hz.ddbm.pc.plugin;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.coast.Coasts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorPlugin implements Plugin {
    @Override
    public String code() {
        return Coasts.PLUGIN_ERROR_LOG;
    }

    @Override
    public void preAction(FsmContext ctx) {

    }

    @Override
    public void postAction(State lastNode, FsmContext ctx) {

    }

    @Override
    public void onActionException(State preNode, Exception e, FsmContext ctx) {

    }

    @Override
    public void onActionFinally(FsmContext ctx) {

    }


}
