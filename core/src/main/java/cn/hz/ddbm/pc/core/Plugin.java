package cn.hz.ddbm.pc.core;

/**
 * 插件:
 * ___1,实现监控、统计等功能和业务功能分离。
 * ___2,如果业务和插件之间需要信息交互，通过上下文完成。
 */

public interface Plugin<S extends Enum<S>> extends ValueObject {
    String code();

    void preAction(String name, FsmContext<S, ?> ctx);

    void postAction(String name, S lastNode, FsmContext<S, ?> ctx);

    void onActionException(String actionName, S preNode, Exception e, FsmContext<S, ?> ctx);

    void onActionFinally(String name, FsmContext<S, ?> ctx);

    void postRoute(String routerName, S preNode, FsmContext<S, ?> ctx);

    void onRouteExcetion(String routerName, Exception e, FsmContext<S, ?> ctx);


}
