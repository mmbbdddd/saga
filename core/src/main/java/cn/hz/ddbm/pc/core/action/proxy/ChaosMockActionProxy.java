package cn.hz.ddbm.pc.core.action.proxy;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.Transition;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.NoSuchRecordException;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import cn.hz.ddbm.pc.core.utils.RandomUitl;

import java.util.Set;

/**
 * 1，mock bean
 * 2,注入混沌错误
 *
 * @param <S>
 */
public class ChaosMockActionProxy<S extends State> implements QueryAction<S>, SagaAction, CommandAction<S> {
    String actionName;

    public ChaosMockActionProxy(String actionName) {
        this.actionName = actionName;
    }


    @Override
    public String beanName() {
        return actionName;
    }

    @Override
    public S execute(FsmContext ctx) throws Exception {
        return null;
    }

    @Override
    public S queryState(FsmContext ctx) throws Exception {
        return null;
    }

    @Override
    public void exec(FsmContext ctx) throws Exception {

    }

    @Override
    public Boolean executeQuery(FsmContext ctx) throws NoSuchRecordException {
        return null;
    }

    @Override
    public void rollback(FsmContext ctx) {

    }

    @Override
    public Boolean rollbackQuery(FsmContext ctx) throws NoSuchRecordException {
        return null;
    }


//    @Override
//    public S queryState(FsmContext<S, ?> ctx) throws Exception {
//        Profile<S>    profile    = ctx.getProfile();
//        Transition<S> transition = ctx.getTransition();
//
//        S                    from        = transition.getFrom();
//        String               event       = transition.getEvent();
//        Set<Pair<S, Double>> statusRadio = profile.getMaybeResults().get(from, event);
//        String               key         = String.format("%s_%s", from.name(), event);
//        return RandomUitl.selectByWeight(key, statusRadio);
//
//    }


}
