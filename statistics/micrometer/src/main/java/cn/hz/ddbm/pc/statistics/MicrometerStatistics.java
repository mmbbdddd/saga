package cn.hz.ddbm.pc.statistics;


import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;

import java.io.Serializable;

public class MicrometerStatistics implements StatisticsSupport {
    @Override
    public Coast.StatisticsType code() {
        return null;
    }

    @Override
    public void increment(String flowName, Serializable flowId, State state, String variableName) {

    }

    @Override
    public Long get(String flowName, Serializable flowId, State state, String variableName) {
        return null;
    }
//    @Override
//    public void increment(String flowName, Serializable flowId, State node, String key) {
//
//    }
//
//    @Override
//    public Long get(String flowName, Serializable flowId, State node, String key) {
//        return null;
//    }
}
