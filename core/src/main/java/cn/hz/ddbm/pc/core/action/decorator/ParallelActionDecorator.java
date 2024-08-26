package cn.hz.ddbm.pc.core.action.decorator;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.ParallelAction;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ParallelActionDecorator extends MultiActionDecorator {
    List<ParallelAction> actions;
    Enum                 su;
    Enum                 fail;

    public ParallelActionDecorator(String actionNames, Enum su, Enum fail, Enum failover, List<ParallelAction> actions) {
        super(actionNames, failover);
        Assert.notNull(actions, "ParallelAction.actions is null");
        Assert.notNull(failover, "ParallelAction.failover is null");
        this.actions = actions;
        this.fail    = fail;
        this.su      = su;
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {
        for (ParallelAction action : actions) {
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
        List<ParallelAction.ActionResultType> results = actions.stream().map(action -> {
            try {
                Enum state = action.query(ctx);
                return action.resultType(state);
            } catch (Exception e) {
                return action.resultType(e);
            }
        }).collect(Collectors.toList());
        //全部任务完成，即认为完成。
        if (results.stream().allMatch(result -> result.equals(ParallelAction.ActionResultType.FINE))) {
            return su;
        } else if (results.stream()
                .allMatch(result -> result.equals(ParallelAction.ActionResultType.FINE) || result.equals(ParallelAction.ActionResultType.ERROR))) {
            //并非全部任务完成，则认为失败
            return fail;
        } else {
            //依然有任务未完成
            return failover;
        }
    }


}
