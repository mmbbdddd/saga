package cn.hz.ddbm.pc.core.action.decorator;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.MergeAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ParallelActionDecorator extends MultiActionDecorator {
    Boolean allThrough;
    MergeAction mergeQuery;

    public ParallelActionDecorator(Boolean allThrough, Fsm.Transition transition, List<Action> actions) {
        super(transition, actions);
        this.allThrough = allThrough;
    }

    @Override
    public void execute(FsmContext ctx) throws Exception {
        for (CommandAction sCommandAction : commandActions) {
            InfraUtils.getActionExecutorService().submit(() -> {
                sCommandAction.execute(ctx);
                return null;
            });
        }
    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        List<MergeAction.ActionResult> results = queryActions.stream().map(queryAction -> {
            try {
                Enum state = queryAction.query(ctx);
                return MergeAction.ActionResult.of(state);
            } catch (Exception e) {
                return MergeAction.ActionResult.exception(null,e);
            }
        }).collect(Collectors.toList());
        return mergeQuery.mergeResult(allThrough,results);
    }


}
