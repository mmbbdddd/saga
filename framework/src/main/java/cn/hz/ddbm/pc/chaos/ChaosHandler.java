package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.ChaosService;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ChaosHandler {
    ChaosService chaosService;

    public ChaosHandler(ChaosService chaosService) {
        this.chaosService = chaosService;
    }

    public void handle(ChaosTargetType chaosTargetType, Object proxy, Method method, Object[] args) throws Throwable {
        List<ChaosRule> rules = chaosService.chaosRules();
        if (null != rules) {
            for (ChaosRule rule : rules) {
                if (rule.match(chaosTargetType, proxy, method, args) && rule.probabilityIsTrue()) {
                    rule.raiseException();
                }
            }
        }
    }

    public Object generateResult(ChaosTargetType type, Object target, Method method, Object[] args) {
        switch (type) {
            case sagaAction: {
                String                    key         = target.getClass().getSimpleName() + "." + method.getName();
                Set<Pair<Object, Double>> sagaResults = new HashSet<>();
                sagaResults.add(Pair.of(true, 0.7));
                sagaResults.add(Pair.of(false, 0.2));
                sagaResults.add(Pair.of(null, 0.1));
                return RandomUitl.selectByWeight(key, sagaResults);
            }
            case fsmAction: {
                String                    key         = target.getClass().getSimpleName() + "." + method.getName();
                Set<Pair<Object, Double>> sagaResults = getFsmSagaResults(target.getClass());
                return RandomUitl.selectByWeight(key, sagaResults);
            }
        }
        return null;
    }

    private Set<Pair<Object, Double>> getFsmSagaResults(Class<?> aClass) {
        List<Pair<String, Object>>              fsmSagaResults = chaosService.getFsmSagaRules();
        Map<String, List<Pair<String, Object>>> temp           = fsmSagaResults.stream().collect(Collectors.groupingBy(Pair::getKey));
        Map<String, Set<Pair<Object, Double>>>  sagaResultMap  = new HashMap<>();
        temp.forEach((clz, listPair) -> {
            sagaResultMap.put(clz, listPair.stream().map(pair -> Pair.of(pair.getValue(), Math.random())).collect(Collectors.toSet()));
        });
        return sagaResultMap.get(aClass.getSimpleName());
    }
}
