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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 混沌发生器
 * 1，根据规则混沌
 * 2，根据规则生成交易结果
 * <p>
 * 规则格式
 * action：ChaosTargetType值。
 * type
 */
public class ChaosHandlerImpl implements ChaosHandler {
    Set<Pair<ChaosRule, Double>> errorRules;
    Set<Pair<ChaosRule, Double>> resultRules;

    public ChaosHandlerImpl() {
        this.errorRules  = new HashSet<>();
        this.resultRules = new HashSet<>();
    }

    public void setChaosRules(List<ChaosRule> rules) {
        if (null == rules || rules.isEmpty()) return;
        this.errorRules  = new HashSet<>();
        this.resultRules = new HashSet<>();
        this.errorRules  = rules.stream()
                .filter(r -> r.getType().equals(ChaosRuleType.EXCEPTION))
                .map(r -> Pair.of(r, r.toWeight()))
                .collect(Collectors.toSet());
        this.resultRules = rules.stream().filter(r -> r.getType().equals(ChaosRuleType.RESULT)).map(r -> Pair.of(r, r.toWeight())).collect(Collectors.toSet());

    }

    /**
     * 业务逻辑混沌注入
     *
     * @throws Exception
     */
    public void handle() throws Exception {
        if (null != errorRules) {
            ChaosRule rule = RandomUitl.selectByWeight("error", errorRules);
            if (rule.isException()) {
                rule.raiseException();
            }
        }
    }


    /**
     * 模拟生成FsmRouter的结果
     */
    @Override
    public <S extends Enum<S>> S handleRouter(FsmContext<S> ctx, FsmRouter<S> router) {
        Set<Pair<S, Double>> fsmQueryResult = router.getStateExpressions().values().stream().map(s -> Pair.of(s, Math.random())).collect(Collectors.toSet());
        return RandomUitl.selectByWeight("router", fsmQueryResult);
    }

    /**
     * 模拟生成FsmAction.executeQuery的返回对象。
     */
    @Override
    public Object executeQuery(FsmContext<?> ctx) {
        return new Object();
    }


    /**
     * 模拟生成SagaAction.executeQuery的返回对象。
     */
    @Override
    public Boolean executeQuery(SagaContext<?> ctx) {
        Set<Pair<Boolean, Double>> fsmQueryResult = new HashSet<>();
        fsmQueryResult.add(Pair.of(true, 0.8));
        fsmQueryResult.add(Pair.of(true, 0.2));
        return RandomUitl.selectByWeight("SagaAction", fsmQueryResult);
    }

    /**
     * 模拟生成SagaAction.rollbackQuery的返回对象。
     */
    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) {
        Set<Pair<Boolean, Double>> fsmQueryResult = new HashSet<>();
        fsmQueryResult.add(Pair.of(true, 0.8));
        fsmQueryResult.add(Pair.of(true, 0.2));
        return RandomUitl.selectByWeight("SagaAction", fsmQueryResult);

    }


}
