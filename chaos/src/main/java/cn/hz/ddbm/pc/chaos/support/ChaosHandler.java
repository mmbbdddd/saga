package cn.hz.ddbm.pc.chaos.support;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.chaos.ChaosRule;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
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
public class ChaosHandler {
    Set<Pair<ChaosRule, Double>> errorRules;

    public ChaosHandler() {
        this.errorRules = new HashSet<>();
    }

    public void setChaosRules(List<ChaosRule> rules) {
        if (null == rules) {
            rules = new ArrayList<>();
        }
        this.errorRules = rules.stream()
                .map(r -> Pair.of(r, r.getWeight()))
                .collect(Collectors.toSet());

    }

    /**
     * 业务逻辑混沌注入
     *
     * @throws Exception
     */
    public void handle() throws Exception {
        if (null != errorRules && !errorRules.isEmpty()) {
            ChaosRule rule = RandomUitl.selectByWeight("error", errorRules);
            if (rule.isException()) {
                rule.raiseException();
            }
        }
    }


    public Boolean sagaRouter(SagaContext ctx) {
        String sagaMode = System.getProperty(Coast.SAGA.CHAOS_MODE);
        if (Objects.equals(sagaMode, Coast.SAGA.CHAOS_TRUE)) {
            return true;
        } else if (Objects.equals(sagaMode, Coast.SAGA.CHAOS_FALSE)) {
            return false;
        } else {
            Set<Pair<Boolean, Double>> results = new HashSet<>();
            results.add(Pair.of(Boolean.TRUE, Coast.SAGA.CHAOS_TRUE_WEIGHT));
            results.add(Pair.of(Boolean.FALSE, Coast.SAGA.CHAOS_FALSE_WEIGHT));
            return RandomUitl.selectByWeight("SAGA_ROUTER", results);
        }

    }
}
