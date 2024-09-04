package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.chaos.ChaosHandler;
import cn.hz.ddbm.pc.newcore.chaos.ChaosTargetType;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;
import cn.hz.ddbm.pc.newcore.infra.Locker;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;
import cn.hz.ddbm.pc.newcore.infra.StatusManager;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChaosHandlerImpl implements ChaosHandler {
    Map<Pair<String, String>, Set<Pair<ChaosRule, Double>>> chaosRuleMap;
    Map<Pair<String, String>, Set<Pair<ChaosRule, Double>>> resultMap;

    public ChaosHandlerImpl() {
        this.chaosRuleMap = new HashMap<>();
        this.resultMap    = new HashMap<>();
    }

    public void setChaosRules(List<ChaosRule> rules) {
        if (null == rules || rules.isEmpty()) return;
        Map<Pair<String, String>, List<Triple<String, String, ChaosRule>>> t1 = rules.stream()
                .filter(r -> r.getAction().equals("exception"))
                .map(r -> Triple.of(r.getType(), r.getMethod(), r))
                .collect(Collectors.groupingBy(t -> Pair.of(t.getLeft(), t.getMiddle())));
        Map<Pair<String, String>, List<Triple<String, String, ChaosRule>>> t2 = rules.stream()
                .filter(r -> r.getAction().equals("result"))
                .map(r -> Triple.of(r.getType(), r.getMethod(), r))
                .collect(Collectors.groupingBy(t -> Pair.of(t.getLeft(), t.getMiddle())));
        this.chaosRuleMap = new HashMap<>();
        this.resultMap    = new HashMap<>();
        t1.forEach((pair, triple) -> {
            this.chaosRuleMap.put(pair, triple.stream()
                    .map(t -> Pair.of(t.getRight(), t.getRight().toWeight()))
                    .collect(Collectors.toSet()));
            //插入正常执行概率
            this.chaosRuleMap.get(pair).add(Pair.of(ChaosRule.DEFAULT, 1.0));
        });
        t2.forEach((pair, triple) -> {
            this.resultMap.put(pair, triple.stream()
                    .map(t -> Pair.of(t.getRight(), t.getRight().toWeight()))
                    .collect(Collectors.toSet()));
        });

    }

    public void handle(ChaosTargetType chaosTargetType, Object proxy, Method method, Object[] args) throws Exception {
        Class                        clz   = getTargetClass(chaosTargetType);
        Set<Pair<ChaosRule, Double>> rules = chaosRuleMap.get(Pair.of(clz.getSimpleName(), method.getName()));
        if (null != rules) {
            String    classSimpleName = clz.getSimpleName();
            String    key             = classSimpleName + "." + method.getName();
            ChaosRule rule            = RandomUitl.selectByWeight(key, rules);
            if (rule.isException()) {
                rule.raiseException();
            }
        }
    }

    private Class getTargetClass(ChaosTargetType type) {
        Class clz = null;
        switch (type) {
            case status:
                return StatusManager.class;
            case session:
                return SessionManager.class;
            case lock:
                return Locker.class;
            case sagaAction:
                return SagaAction.class;
            default:
                return FsmAction.class;
        }
    }

    public Object generateResult(ChaosTargetType type, Object proxy, Method method, Object[] args) {
        Class                        clz             = getTargetClass(type);
        String                       key             = clz.getSimpleName() + "." + method.getName();
        Set<Pair<ChaosRule, Double>> sagaResultRules = resultMap.get(Pair.of(clz.getSimpleName(), method.getName()));
        if (null != sagaResultRules) {
            ChaosRule rule = RandomUitl.selectByWeight(key, sagaResultRules);
            return rule.toValue();
        } else {
            return null;
        }
    }


    @Override
    public <S extends Enum<S>> S handleRouter(FsmContext<S> ctx, FsmRouter<S> router) {
        Set<Pair<ChaosRule, Double>> fsmQueryResult = getOrBuildRouterRule(router);
        return (S) RandomUitl.selectByWeight(router.toString(), fsmQueryResult).toValue();
    }


    @Override
    public Object executeQuery(FsmContext<?> ctx) {
        return null;
    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) {
        return null;
    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) {
        return null;
    }

    private <S extends Enum<S>> Set<Pair<ChaosRule, Double>> getOrBuildRouterRule(FsmRouter<S> router) {
        return null;
    }
}
