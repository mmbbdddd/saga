package cn.hz.ddbm.pc.core.action.actiondsl;

import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

import java.util.List;

public abstract class MultiAction implements SagaAction  {
    Fsm.Transition       transition;
    List<CommandAction > commandActions;
    List<QueryAction >   queryActions;

    public MultiAction(Fsm.Transition transition, List<Action> actions) {
        this.transition     = transition;
        this.commandActions = null;
        this.queryActions   = null;
    }

    @Override
    public String beanName() {
        return transition.getActionDsl();
    }


}
