package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
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

    String action;
    String type;
    String method;
    String value;
    String weight;
    String value_type;

    public Object toValue() {
        Class<Enum> valueType = getValueEnumType();
        if (null != valueType && !StrUtil.isBlank(value)) {
            try {
                return Enum.valueOf(valueType, value);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    private Class<Enum> getValueEnumType() {
        if (value_type != null) {
            if (!StrUtil.isBlank(value_type)) {
                try {
                    return ClassUtil.loadClass(value_type);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }

    private Class<Exception> getValueExceptionType() {
        if (value_type != null) {
            if (!StrUtil.isBlank(value_type)) {
                try {
                    return ClassUtil.loadClass(value_type);
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
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
