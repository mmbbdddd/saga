package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.newcore.chaos.ChaosTargetType;
import lombok.Data;

@Data
public class ChaosRule {
    public static final ChaosRule DEFAULT = ChaosRule.defaultOf();

    private static ChaosRule defaultOf() {
        ChaosRule rule = new ChaosRule();
        rule.weight = "1.0";
        rule.value  = "true";
        return rule;
    }

    ChaosTargetType target;
    ChaosRuleType   type;
    String          method;
    String          value;
    String          weight;
    Class           valueType;

    public Object toValue() {
        Class<Enum> valueType = getValueEnumType();
        if (null != valueType && !StrUtil.isBlank(value)) {
            try {
                return Enum.valueOf(valueType, value);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private Class<Enum> getValueEnumType() {
        if (valueType != null) {
            return (Class<Enum>) valueType;
        }
        return null;
    }

    private Class<Exception> getValueExceptionType() {
        if (valueType != null) {
            return (Class<Exception>) valueType;
        }
        return null;
    }

    public Double toWeight() {
        return Double.valueOf(weight);
    }

    public boolean isException() {
        return !StrUtil.isBlank(value) && value.endsWith("Exception");
    }

    public void raiseException() throws Exception {
        Exception e = getValueExceptionType().newInstance();
        throw e;
    }
}
