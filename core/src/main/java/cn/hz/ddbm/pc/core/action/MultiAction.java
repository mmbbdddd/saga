package cn.hz.ddbm.pc.core.action;


import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public class MultiAction<S extends Enum<S>> implements Action<S>, Action.QueryAction<S>, Action.SagaAction<S> {
    String       actionNames;
    List<Action<S>> actions;

    public MultiAction(String actionNames, List<Action<S>> actions) {
        this.actionNames = actionNames;
        this.actions     = actions;
    }

    @Override
    public String beanName() {
        return actionNames;
    }

    @Override
    public void execute(FsmContext<S,?> ctx) throws Exception {
        for (Action<S> action : this.actions) {
            action.execute(ctx);
        }
    }


    @Override
    public S query(FsmContext<S,?> ctx) throws Exception {
        QueryAction<S> queryAction = actions.stream()
                .filter(a->a instanceof QueryAction).map(a->(QueryAction)a).findFirst().get();
        return queryAction.query(ctx);
    }


}
