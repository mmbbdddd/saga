package cn.hz.ddbm.pc.core.action.impl;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.SagaAction;

import java.util.List;
import java.util.stream.Collectors;

public class SerialAction extends MultiAction {

    public SerialAction(String actionNames, Enum failover, List<Action> actions) {
        super(actionNames, failover, actions);
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {
        for (SagaAction action : commandActions) {
            action.execute(ctx);
        }
    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        List<Enum> results = queryActions.stream().map(action -> {
            try {
                return action.query(ctx);
            } catch (Exception e) {
                return failover();
            }
        }).collect(Collectors.toList());
        return null;
    }


}
