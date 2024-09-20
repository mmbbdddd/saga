package cn.hz.ddbm.pc.newcore.chaos;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.chaos.ChaosRule;

import java.util.HashSet;
import java.util.Set;

public interface ChaosConfig {


    Set<Pair<ChaosRule, Double>> infraChaosRule();

    Set<Pair<Boolean, Double>> sagaFailoverResult();

    static ChaosConfig badOf() {
        return new ChaosConfig() {
            @Override
            public Set<Pair<ChaosRule, Double>> infraChaosRule() {
                Set<Pair<ChaosRule, Double>> s = new HashSet<>();
                s.add(Pair.of(new ChaosRule(true), 4.0));
                s.add(Pair.of(new ChaosRule(RuntimeException.class), 1.0));
                s.add(Pair.of(new ChaosRule(Exception.class), 1.0));
                return s;
            }

            @Override
            public Set<Pair<Boolean, Double>> sagaFailoverResult() {
                Set<Pair<Boolean, Double>> s = new HashSet<>();
                s.add(Pair.of(Boolean.TRUE, 1.0));
                s.add(Pair.of(Boolean.FALSE, 1.0));
                return s;
            }
        };
    }

    static ChaosConfig goodOf() {
        return new ChaosConfig() {
            @Override
            public Set<Pair<ChaosRule, Double>> infraChaosRule() {
                Set<Pair<ChaosRule, Double>> s = new HashSet<>();
                s.add(Pair.of(new ChaosRule(true), 10.0));
                return s;
            }

            @Override
            public Set<Pair<Boolean, Double>> sagaFailoverResult() {
                Set<Pair<Boolean, Double>> s = new HashSet<>();
                s.add(Pair.of(Boolean.TRUE, 10.0));
                return s;
            }
        };
    }

    static ChaosConfig defaultOf() {
        return new ChaosConfig() {
            @Override
            public Set<Pair<ChaosRule, Double>> infraChaosRule() {
                Set<Pair<ChaosRule, Double>> s = new HashSet<>();
                s.add(Pair.of(new ChaosRule(true), 9.0));
                s.add(Pair.of(new ChaosRule(RuntimeException.class), 0.5));
                s.add(Pair.of(new ChaosRule(Exception.class), 10.5));
                return s;
            }

            @Override
            public Set<Pair<Boolean, Double>> sagaFailoverResult() {
                Set<Pair<Boolean, Double>> s = new HashSet<>();
                s.add(Pair.of(Boolean.TRUE, 9.0));
                s.add(Pair.of(Boolean.FALSE, 1.0));
                return s;
            }
        };
    }
}
