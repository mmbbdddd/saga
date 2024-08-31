package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hz.ddbm.pc.newcore.State;
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
            statisticsSupport.increment(flowName, flowId, node, variable);
        } catch (Exception e) {
        }
    }

    @Override
    public Long get(String flowName, Serializable flowId, State node, String variable) {
        try {
            return statisticsSupport.get(flowName, flowId, node, variable);
        } catch (Exception e) {
            //
            return 0l;
        }
    }
}
