package cn.hz.ddbm.pc.core.action.impl;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

import java.util.List;
import java.util.stream.Collectors;

public class SerialAction extends MultiAction {
    List<CommandAction> commandActions;
    QueryAction         queryAction;

    public SerialAction(String actionNames, Enum failover, List<Action> actions) {
        super(actionNames, failover);
        this.commandActions = commandActions;
        this.queryAction    = queryAction;
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
