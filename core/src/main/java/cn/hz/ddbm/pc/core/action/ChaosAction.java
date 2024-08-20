package cn.hz.ddbm.pc.core.action;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.*;
import cn.hz.ddbm.pc.core.utils.RandomUitl;

import java.util.Set;

public class ChaosAction<S extends Enum<S>> implements Action<S>, Action.QueryAction<S>, Action.SagaAction<S> {
    String actionName;


    @Override
    public String beanName() {
        return actionName;
    }

    @Override
    public void execute(FsmContext<S, ?> ctx) throws Exception {


    }


    @Override
    public S query(FsmContext<S, ?> ctx) throws Exception {
        Profile<S>          profile    = ctx.getProfile();
        BaseProcessor<?, S> actionBase = ctx.getExecutor();
        if (actionBase.getFsmRecord().getType().equals(Fsm.TransitionType.TO)) {
            return actionBase.getFsmRecord().getTo();
        } else {
            S                    from        = actionBase.getFsmRecord().getFrom();
            String               event       = actionBase.getFsmRecord().getEvent();
            Set<Pair<S, Double>> statusRadio = profile.getMaybeResults().get(from, event);
            String               key         = String.format("%s_%s", from.name(), event);
            return RandomUitl.selectByWeight(key, statusRadio);
        }
    }

}
