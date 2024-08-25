package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;
import java.util.stream.Collectors;

public class SerialAction<S extends Enum<S>> extends MultiAction<S> {

    public SerialAction(String actionNames, List<Action<S>> actions) {
        super(actionNames, actions);
    }

    @Override
    public void execute(FsmContext<S, ?> ctx) throws Exception {
        for (Action<S> action : getExecuteActions(false, Action.class)) {
            action.execute(ctx);
        }
    }

    @Override
    public S query(FsmContext<S, ?> ctx) throws Exception {
        List<QueryAction> queryActions = getExecuteActions(true, QueryAction.class);
        List<S> actionResults = queryActions.stream().map(action -> {
            try {
                return (S) action.query(ctx);
            } catch (Exception e) {
                //
                return failover();
            }
        }).collect(Collectors.toList());
        return null;
    }

    @Override
    public S failover() {
        return null;
    }


    private <T> List<T> getExecuteActions(Boolean isQuery, Class<T> type) {
        return actions.stream()
                      .filter(action -> (isQuery && action instanceof QueryAction) || (!isQuery && !(action instanceof QueryAction)))
                      .map(action -> (T) action)
                      .collect(Collectors.toList());
    }

}
