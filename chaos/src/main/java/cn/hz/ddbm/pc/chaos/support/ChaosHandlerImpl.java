package cn.hz.ddbm.pc.chaos.support;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.chaos.ChaosHandler;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;

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
        this.errorRules  = rules.stream()
                .filter(r -> r.getType().equals(ChaosRuleType.EXCEPTION))
                .map(r -> Pair.of(r, r.getWeight()))
                .collect(Collectors.toSet());
        this.resultRules = rules.stream().filter(r -> r.getType().equals(ChaosRuleType.RESULT)).map(r -> Pair.of(r, r.getWeight())).collect(Collectors.toSet());

    }

    /**
     * 业务逻辑混沌注入
     *
     * @throws Exception
     */
    public void handle() throws Exception {
        if (null != errorRules&& !resultRules.isEmpty()) {
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


}
