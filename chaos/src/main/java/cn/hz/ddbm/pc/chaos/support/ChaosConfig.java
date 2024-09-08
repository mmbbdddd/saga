package cn.hz.ddbm.pc.chaos.support;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.chaos.ChaosRule;

import java.util.Set;

public interface ChaosConfig {
     Set<Pair<ChaosRule, Double>> infraChaosRule();

    Set<Pair<Boolean, Double>> sagaFailoverResult();
}
