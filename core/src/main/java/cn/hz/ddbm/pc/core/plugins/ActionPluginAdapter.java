package cn.hz.ddbm.pc.core.plugins;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FlowContext;
import cn.hz.ddbm.pc.core.Plugin;

public interface ActionPluginAdapter<S extends Enum<S>> extends Plugin<S>, Action<S> {

    @Override
    default String code() {
        return beanName();
    }

    @Override
    default void preAction(String name, FlowContext<S, ?> ctx) {

    }

    @Override
    default void postAction(String name, S lastNode, FlowContext<S, ?> ctx) {

    }

    @Override
    default void onActionException(String actionName, S preNode, Exception e, FlowContext<S, ?> ctx) {

    }

    @Override
    default void onActionFinally(String name, FlowContext<S, ?> ctx) {

    }

    @Override
    default void postRoute(String routerName, S preNode, FlowContext<S, ?> ctx) {

    }

    @Override
    default void onRouteExcetion(String routerName, Exception e, FlowContext<S, ?> ctx) {

    }
}
