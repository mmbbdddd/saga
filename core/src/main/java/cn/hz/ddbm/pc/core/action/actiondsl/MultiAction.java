package cn.hz.ddbm.pc.core.action.actiondsl;

import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

import java.util.List;
import java.util.stream.Collectors;

public abstract class MultiAction implements SagaAction {
    Fsm.Transition      transition;
    List<CommandAction> commandActions;
    List<QueryAction>   queryActions;

    public MultiAction(Fsm.Transition transition, List<Action> actions) {
        this.transition     = transition;
        this.commandActions = actions.stream().filter(t -> t instanceof CommandAction).map(CommandAction.class::cast).collect(Collectors.toList());
        this.queryActions   = actions.stream().filter(t -> t instanceof QueryAction).map(QueryAction.class::cast).collect(Collectors.toList());
    }

    @Override
    public String beanName() {
        return transition.getActionDsl();
    }


}
