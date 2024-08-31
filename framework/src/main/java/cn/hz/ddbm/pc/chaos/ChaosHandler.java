package cn.hz.ddbm.pc.chaos;

import cn.hz.ddbm.pc.ChaosService;

import java.lang.reflect.Method;
import java.util.List;

public class ChaosHandler {
    ChaosService chaosService;
    public void handle(ChaosTargetType chaosTargetType, Object proxy, Method method, Object[] args) throws Throwable {
        List<ChaosRule> rules      = chaosService.chaosRules();
        if (null != rules) {
            for (ChaosRule rule : rules) {
                if (rule.match(chaosTargetType, proxy, method, args) && rule.probabilityIsTrue()) {
                    rule.raiseException();
                }
            }

        }

//        if (proxy instanceof Action) {
//            FsmContext ctx     = (FsmContext) args[0];
//            Profile    profile = ctx.getProfile();
//            if (ctx.getIsChaos()) {
//                Transition transition = ctx.getTransition();
//                State      nextNode   = null;
//
//                State  from  = transition.getFrom();
//                String event = transition.getEvent();
////                Set<Pair<S, Double>> statusRadio = profile.getMaybeResults().get(from, event);
////                String               key         = String.format("%s_%s", from.name(), event);
////                nextNode = RandomUitl.selectByWeight(key, statusRadio);
//                ctx.setNextNode(nextNode);
//            }
//        }
    }

    public Object generateResult(ChaosTargetType type, Object target, Method method, Object[] args) {
        return null;
    }
}
