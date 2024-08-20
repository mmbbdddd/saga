package cn.hz.ddbm.pc.core;


import cn.hz.ddbm.pc.core.exception.wrap.ActionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.core.log.Logs;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import lombok.Getter;

import java.util.List;

/**
 * @Description 工作流/状态机的原子任务执行器。
 * @Author wanglin
 * @Date 2024/8/7 22:59
 * @Version 1.0.0
 **/

@Getter
public abstract class BaseProcessor<A extends Action<S>, S extends Enum<S>> {
    final Fsm.Transition<S> fsmRecord;
    final List<Plugin>      plugins;


    public BaseProcessor(Fsm.Transition<S> fsmRecord, List<Plugin> plugins) {
        this.plugins   = plugins;
        this.fsmRecord = fsmRecord;
    }


    public abstract A action(FsmContext<S, ?> ctx);

//    public Action<S> action(FlowContext<S, ?> ctx) {
//        if (null == this.action) {
//            synchronized (this) {
//                this.action = initAction(ctx);
//            }
//        }
//        return this.action;
//    }
//
//    protected Action<S> initAction(FlowContext<S, ?> ctx) {
//        return Action.of(actionDsl, ctx);
//    }


//    private S nextNode(S lastNode, FlowContext<S, ?> ctx) throws RouterException {
//        try {
//            S nextNode = route(ctx);
//            postRoutePlugin(ctx.getFlow(), lastNode, ctx);
//            return nextNode;
//        } catch (Exception e) {
//            ctx.getStatus().flush(Coasts.EVENT_DEFAULT, failover(), ctx.getFlow());
//            onRouterExceptionPlugin(ctx.getFlow(), e, ctx);
//            throw new RouterException(e);
//        }
//    }


    protected void preActionPlugin(Fsm<S> flow, FsmContext<S, ?> ctx) {
        plugins.forEach((plugin) -> {
            InfraUtils.getPluginExecutorService().submit(() -> {
                try {
                    plugin.preAction(this.action(ctx).beanName(), ctx);
                } catch (Exception e) {
                    Logs.error.error("{},{}", ctx.getFlow().name, ctx.getId(), e);
                }
            });
        });
    }

    protected void postActionPlugin(Fsm<S> flow, S lastNode, FsmContext<S, ?> ctx) {
        plugins.forEach((plugin) -> {
            InfraUtils.getPluginExecutorService().submit(() -> {
                try {
                    plugin.postAction(this.action(ctx).beanName(), lastNode, ctx);
                } catch (Exception e) {
                    Logs.error.error("{},{}", ctx.getFlow().name, ctx.getId(), e);
                }
            });
        });
    }

    protected void onActionExceptionPlugin(Fsm<S> flow, S preNode, Exception e, FsmContext<S, ?> ctx) {
        plugins.forEach((plugin) -> {
            InfraUtils.getPluginExecutorService().submit(() -> {
                try {
                    plugin.onActionException(this.action(ctx).beanName(), preNode, e, ctx);
                } catch (Exception e2) {
                    Logs.error.error("{},{}", ctx.getFlow().name, ctx.getId(), e2);
                }
            });
        });
    }

    protected void onActionFinallyPlugin(Fsm<S> flow, FsmContext<S, ?> ctx) {
        plugins.forEach((plugin) -> {
            InfraUtils.getPluginExecutorService().submit(() -> {
                try {
                    plugin.onActionFinally(this.action(ctx).beanName(), ctx);
                } catch (Exception e) {
                    Logs.error.error("{},{}", ctx.getFlow().name, ctx.getId(), e);
                }
            });
        });
    }


    public abstract void execute(FsmContext<S, ?> ctx) throws ActionException, StatusException;
}
