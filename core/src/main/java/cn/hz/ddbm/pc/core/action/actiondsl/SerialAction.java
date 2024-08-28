package cn.hz.ddbm.pc.core.action.actiondsl;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Transition;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;

import java.util.List;

public class SerialAction extends MultiAction {
    QueryAction queryAction;

    public SerialAction(Transition transition, List<Action> actions) {
        super(transition, actions);
        Assert.isTrue(queryActions.size() == 1, "ParallelActionDecorator.queryActions.size !=1");
        this.queryAction = queryActions.get(0);
    }

    @Override
    public void execute(FsmContext ctx) throws Exception {
        for (CommandAction sCommandAction : commandActions) {
            sCommandAction.execute(ctx);
        }
    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return queryAction.query(ctx);
    }
}
