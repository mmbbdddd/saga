package cn.hz.ddbm.pc.newcore.chaos;

import cn.hz.ddbm.pc.newcore.utils.RandomUitl;
import lombok.Setter;

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

    private ChaosConfig getChaosConfig() {
        return null == chaosConfig ? ChaosConfig.defaultOf() : chaosConfig;
    }

    /**
     * 业务逻辑混沌注入
     *
     * @throws Exception
     */
    public void infraChaos() throws Exception {
        ChaosRule rule = RandomUitl.selectByWeight("infraChaosRule", getChaosConfig().infraChaosRule());
        if (rule.isException()) {
            rule.raiseException();
        }
    }


    public Boolean sagaRouter() {
        return RandomUitl.selectByWeight("sagaFailoverResult", getChaosConfig().sagaFailoverResult());
    }


}
