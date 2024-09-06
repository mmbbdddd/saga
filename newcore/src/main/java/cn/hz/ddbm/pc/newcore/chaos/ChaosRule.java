package cn.hz.ddbm.pc.newcore.chaos;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class ChaosRule {
    ChaosRuleType type;
    Class         target;
    Object        value;
    Double        weight;

    private static List<ChaosRule> defaultRules() {
        return null;
    }

    public static Set<Pair<Enum, Double>> defaultFsmRouterResults(Class actionClass) {
        Map<Class, List<ChaosRule>> ruleMaps = defaultRules().stream().collect(Collectors.groupingBy(
                ChaosRule::getTarget
        ));
        return ruleMaps.get(actionClass).stream().map(ChaosRule::toFsmRouterResult).collect(Collectors.toSet());
    }


    public static Set<Pair<Boolean, Double>> defaultSagaResults() {
        Map<Class, List<ChaosRule>> ruleMaps = defaultRules().stream().collect(Collectors.groupingBy(
                ChaosRule::getTarget
        ));
        return ruleMaps.get(RemoteSagaAction.class).stream().map(ChaosRule::toSagaActionResult).collect(Collectors.toSet());
    }


    public ChaosRule(ChaosRuleType type, Class target, Object value, Double weight) {
        this.type   = type;
        this.target = target;
        this.value  = value;
        this.weight = weight;
    }


    public boolean isException() {
        try {
            if (null != value && Throwable.class.isAssignableFrom((Class<?>) value)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void raiseException() throws Exception {
        Class     type = (Class) value;
        Exception e    = (Exception) type.newInstance();
        throw e;
    }


    public Pair<Enum, Double> toFsmRouterResult() {
        Assert.isTrue(Enum.class.isAssignableFrom(value.getClass()), "类型不匹配Enum");
        return Pair.of((Enum) value, weight);
    }

    public Pair<Boolean, Double> toSagaActionResult() {
        Assert.isTrue(value instanceof Boolean, "类型不匹配Boolean");
        return Pair.of((Boolean) value, weight);
    }

}
