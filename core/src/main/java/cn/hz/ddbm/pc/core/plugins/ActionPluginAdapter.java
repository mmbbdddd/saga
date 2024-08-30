package cn.hz.ddbm.pc.core.plugins;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Plugin;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.action.Action;

public interface ActionPluginAdapter  extends Plugin , Action  {

    @Override
    default String code() {
        return beanName();
    }

    @Override
    default void preAction( FsmContext  ctx) {

    }

    @Override
    default void postAction( State lastNode, FsmContext  ctx) {

    }

    @Override
    default void onActionException(  State preNode, Exception e, FsmContext ctx) {

    }

    @Override
    default void onActionFinally( FsmContext ctx) {

    }


}
