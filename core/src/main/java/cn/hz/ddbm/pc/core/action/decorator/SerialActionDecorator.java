package cn.hz.ddbm.pc.core.action.decorator;

import cn.hutool.core.lang.Assert;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;

import java.util.List;
import java.util.stream.Collectors;

public class SerialActionDecorator extends MultiActionDecorator {
    List<CommandAction> commandActions;
    QueryAction         queryAction;

    public SerialActionDecorator(String actionNames, Enum failover, List<Action> actions) {
        super(actionNames, failover);
        this.commandActions = actions.stream()
                .filter(action -> action instanceof CommandAction)
                .map(action -> (CommandAction) action)
                .collect(Collectors.toList());
        this.queryAction    = actions.stream()
                .filter(action -> action instanceof CommandAction)
                .map(action -> (QueryAction) action)
                .findFirst().get();
        Assert.notNull(this.queryAction, "SerialAction.queryAction is null");
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {
        for (CommandAction action : commandActions) {
            action.execute(ctx);
        }
    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return queryAction.query(ctx);
    }


}
