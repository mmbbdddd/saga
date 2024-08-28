package cn.hz.ddbm.pc.container.chaos;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.Profile;
import cn.hz.ddbm.pc.core.Transition;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.support.Locker;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import cn.hz.ddbm.pc.core.utils.RandomUitl;
import cn.hz.ddbm.pc.profile.ChaosSagaService;
import cn.hz.ddbm.pc.profile.chaos.ChaosRule;
import cn.hz.ddbm.pc.profile.chaos.ChaosTarget;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * 混沌发生器。具体发生规则参见ChaosRule
 */
public class ChaosHandler {

    ChaosSagaService chaosPcService;

    public ChaosHandler(ChaosSagaService chaosPcService) {
        this.chaosPcService = chaosPcService;
    }

    /**
     * 判断当前执行对象有没有触发混沌规则表（chaosRule）
     *
     * @param proxy
     * @param method
     * @param args
     * @throws Throwable
     */
    public <S extends Enum<S>> void handle(Object proxy, Method method, Object[] args) throws Throwable {
        List<ChaosRule> rules      = chaosPcService.chaosRules();
        ChaosTarget     targetType = getTargetType(proxy);

        if (null != rules) {
            for (ChaosRule rule : rules) {
                if (rule.match(targetType, proxy, method, args) && rule.probabilityIsTrue()) {
                    rule.raiseException();
                }
            }
        }

        if (proxy instanceof Action) {
            FsmContext<S, ?> ctx     = (FsmContext<S, ?>) args[0];
            Profile<S>       profile = ctx.getProfile();
            if (ctx.getIsChaos()) {
                Transition<S> transition = ctx.getTransition();
                S             nextNode   = null;
                if (transition.getType().equals(Transition.Type.TO)) {
                    nextNode = transition.getTo();
                } else {
                    S                    from        = transition.getFrom();
                    String               event       = transition.getEvent();
                    Set<Pair<S, Double>> statusRadio = profile.getMaybeResults().get(from, event);
                    String               key         = String.format("%s_%s", from.name(), event);
                    nextNode = RandomUitl.selectByWeight(key, statusRadio);
                }
                ctx.setNextNode(nextNode);
            }
        }
    }

    private ChaosTarget getTargetType(Object proxy) {
        if (proxy instanceof Action) {
            return ChaosTarget.ACTION;
        }
        if (proxy instanceof SessionManager) {
            return ChaosTarget.SESSION;
        }
        if (proxy instanceof StatusManager) {
            return ChaosTarget.STATUS;
        }
        if (proxy instanceof Locker) {
            return ChaosTarget.LOCK;
        }
        return null;
    }


}
