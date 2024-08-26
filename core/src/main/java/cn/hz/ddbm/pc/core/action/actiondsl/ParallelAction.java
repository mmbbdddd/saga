package cn.hz.ddbm.pc.core.action.actiondsl;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.MergeAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.List;
import java.util.stream.Collectors;

public class ParallelAction extends MultiAction {

    Boolean     all;
    MergeAction mergeAction;

    public ParallelAction(Boolean all, Fsm.Transition transition, List<Action> actions) {
        super(transition, actions);
        this.all         = all;
        this.mergeAction = actions.stream().filter(t -> t instanceof MergeAction).map(a -> (MergeAction) a).findFirst().get();
        Assert.notNull(mergeAction, "ParallelAction.mergeAction is null");
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
        List<Pair<?, ?>> results = queryActions.stream().map(queryAction -> {
            try {
                Enum state = queryAction.query(ctx);
                return Pair.of(state, null);
            } catch (Exception e) {
                return Pair.of(null, e);
            }
        }).collect(Collectors.toList());
        return mergeAction.mergeResult(all, results);
    }


}
