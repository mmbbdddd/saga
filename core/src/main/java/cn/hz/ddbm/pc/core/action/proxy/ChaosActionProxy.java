package cn.hz.ddbm.pc.core.action.proxy;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.Transition;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import cn.hz.ddbm.pc.core.utils.RandomUitl;

import java.util.Set;

public class ChaosActionProxy<S extends Enum<S>> implements QueryAction<S>, SagaAction<S> {
    String actionName;

    public ChaosActionProxy(String actionName) {
        this.actionName = actionName;
    }


    @Override
    public String beanName() {
        return actionName;
    }

    @Override
    public void execute(FsmContext<S, ?> ctx) throws Exception {
        InfraUtils.getBean("chaosHandler");
    }


    @Override
    public S query(FsmContext<S, ?> ctx) throws Exception {
        Profile<S>    profile    = ctx.getProfile();
        Transition<S> transition = ctx.getTransition();
        if (transition.getType().equals(Transition.Type.TO)) {
            return transition.getTo();
        } else {
            S                    from        = transition.getFrom();
            String               event       = transition.getEvent();
            Set<Pair<S, Double>> statusRadio = profile.getMaybeResults().get(from, event);
            String               key         = String.format("%s_%s", from.name(), event);
            return RandomUitl.selectByWeight(key, statusRadio);
        }
    }

}
