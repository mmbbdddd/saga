package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.newcore.chaos.ChaosTargetType;
import io.netty.util.internal.StringUtil;
import lombok.Data;

@Data
public class ChaosRule {
    public static final ChaosRule DEFAULT = ChaosRule.defaultOf();

    private static ChaosRule defaultOf() {
        return new ChaosRule(ChaosRuleType.RESULT,true,1.0);
    }

    ChaosRuleType type;
    Object        value;
    Double        weight;

    public ChaosRule(ChaosRuleType type, Object value, Double weight) {
        this.type   = type;
        this.value  = value;
        this.weight = weight;
    }

    public boolean isException() {
        if (null != value && value.equals("Exception")) {
            try {
                Class type = Class.forName(value.toString());
                return Throwable.class.isAssignableFrom(type);
            } catch (ClassNotFoundException e) {
                return false;
            }
        }
        return false;
    }

    public void raiseException() throws Exception {
        Class     type = Class.forName(value.toString());
        Exception e    = (Exception) type.newInstance();
        throw e;
    }
}
