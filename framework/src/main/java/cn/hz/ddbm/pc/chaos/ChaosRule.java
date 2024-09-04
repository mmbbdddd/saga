package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.newcore.chaos.ChaosTargetType;
import io.netty.util.internal.StringUtil;
import lombok.Data;
import org.springframework.util.ClassUtils;

@Data
public class ChaosRule {
    public static final ChaosRule DEFAULT = ChaosRule.defaultOf();

    private static ChaosRule defaultOf() {
        return new ChaosRule(ChaosRuleType.RESULT, true, 1.0);
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
}
