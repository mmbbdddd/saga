package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;
import io.netty.util.internal.StringUtil;
import lombok.Data;

@Data
public class Rule {
    public static final Rule DEFAULT = Rule.defaultOf();

    private static Rule defaultOf() {
        Rule rule = new Rule();
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
        if (null != valueType && !StringUtil.isNullOrEmpty(value)) {
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
            if (!StringUtil.isNullOrEmpty(value_type)) {
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
            if (!StringUtil.isNullOrEmpty(value_type)) {
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
        return !StringUtil.isNullOrEmpty(value) && value.endsWith("Exception");
    }

    public void raiseException() throws Exception {
        Exception e = getValueExceptionType().newInstance();
        throw e;
    }
}
