package cn.hz.ddbm.pc.chaos.support;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.chaos.ChaosRule;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;
import lombok.Setter;

import java.util.*;

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
    @Setter
    ChaosConfig chaosConfig;

    public ChaosHandler() {
        this.chaosConfig = new ChaosConfig() {
            @Override
            public Set<Pair<ChaosRule, Double>> infraChaosRule() {
                Set<Pair<ChaosRule, Double>> s = new HashSet<>();
                s.add(Pair.of(new ChaosRule(true), 8.0));
                s.add(Pair.of(new ChaosRule(RuntimeException.class), 1.0));
                s.add(Pair.of(new ChaosRule(Exception.class), 1.0));
                return s;
            }

            @Override
            public Set<Pair<Boolean, Double>> sagaFailoverResult() {
                Set<Pair<Boolean, Double>> s = new HashSet<>();
                s.add(Pair.of(Boolean.TRUE, 4.0));
                s.add(Pair.of(Boolean.FALSE, 1.0));
                return s;
            }
        };
    }

    /**
     * 业务逻辑混沌注入
     *
     * @throws Exception
     */
    public void infraChaos() throws Exception {
        ChaosRule rule = RandomUitl.selectByWeight("infraChaosRule", chaosConfig.infraChaosRule());
        if (rule.isException()) {
            rule.raiseException();
        }
    }


    public Boolean sagaRouter() {
        return RandomUitl.selectByWeight("sagaFailoverResult", chaosConfig.sagaFailoverResult());
    }


}
