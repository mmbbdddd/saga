package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.chaos.ChaosHandler;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;

import java.io.Serializable;

public class StatisticsSupportProxy implements StatisticsSupport {
    StatisticsSupport statisticsSupport;

    public StatisticsSupportProxy(StatisticsSupport bean) {
        this.statisticsSupport = bean;
    }

    @Override
    public Coast.StatisticsType code() {
        return statisticsSupport.code();
    }

    @Override
    public void increment(String flowName, Serializable flowId, State node, String variable) {
        try {
            SpringUtil.getBean(ChaosHandler.class).statistics();
            statisticsSupport.increment(flowName, flowId, node, variable);
        } catch (Exception e) {
        }
    }

    @Override
    public Long get(String flowName, Serializable flowId, State node, String variable) {
        try {
            SpringUtil.getBean(ChaosHandler.class).statistics();
            return statisticsSupport.get(flowName, flowId, node, variable);
        } catch (Exception e) {
            //
            return 0l;
        }
    }
}
