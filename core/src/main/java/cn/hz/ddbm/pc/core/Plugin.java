package cn.hz.ddbm.pc.core;

/**
 * 插件:
 * ___1,实现监控、统计等功能和业务功能分离。
 * ___2,如果业务和插件之间需要信息交互，通过上下文完成。
 */

public interface Plugin {
    String code();

    void preAction(FsmContext ctx);

    void postAction(State lastNode, FsmContext ctx);

    void onActionException(State preNode, Exception e, FsmContext ctx);

    void onActionFinally(FsmContext ctx);
}
