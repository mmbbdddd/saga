package cn.hz.ddbm.pc.statistics;

import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;

import java.io.Serializable;

public class MicrometerStatistics implements StatisticsSupport {
    @Override
    public void increment(String flowName, Serializable flowId, State node, String key) {

    }

    @Override
    public Long get(String flowName, Serializable flowId, State node, String key) {
        return null;
    }
}
