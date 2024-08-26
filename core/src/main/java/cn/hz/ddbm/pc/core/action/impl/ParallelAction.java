package cn.hz.ddbm.pc.core.action.impl;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public class ParallelAction extends MultiAction {
    List<SagaAction> actions;
    Enum             su;
    Enum             fail;

    public ParallelAction(String actionNames, Enum su, Enum fail, Enum failover, List<SagaAction> actions) {
        super(actionNames, failover);
        Assert.notNull(actions, "ParallelAction.actions is null");
        Assert.notNull(su, "ParallelAction.su is null");
        Assert.notNull(fail, "ParallelAction.fail is null");
        Assert.notNull(failover, "ParallelAction.failover is null");
        this.actions = actions;
        this.su      = su;
        this.fail    = fail;
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {
        for (SagaAction action : actions) {
            InfraUtils.getActionExecutorService().submit(() -> {
                try {
                    action.execute(ctx);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        List<Enum> results = actions.stream().map(action -> {
            try {
                return action.query(ctx);
            } catch (Exception e) {
                return failover();
            }
        }).collect(Collectors.toList());
        //全部任务完成，即认为完成。
        if (results.stream().allMatch(result -> result.equals(su))) {
            return su;
        } else if (results.stream().allMatch(result -> result.equals(su) || result.equals(fail))) {
            //并非全部任务完成，则认为失败
            return fail;
        } else {
            //依然有任务未完成
            return failover;
        }

    }

}
