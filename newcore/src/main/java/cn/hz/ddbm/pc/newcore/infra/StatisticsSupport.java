package cn.hz.ddbm.pc.newcore.infra;


import cn.hz.ddbm.pc.newcore.State;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @Description 统计接口
 * @Author wanglin
 * @Date 2024/8/7 22:34
 * @Version 1.0.0
 **/


public interface StatisticsSupport {
    void increment(String flowName, Serializable flowId, State node, String variable);

    Long get(String flowName, Serializable flowId, State node, String variable);

    @Data
    class VariableCalcConfig<A extends VariableConfig> {
        String         variable;
        StatisticsAlgo algo;
        A              args;
    }


    /**
     * todo
     */
    enum StatisticsAlgo {
        WINDOWS, RANGE
    }

    @Data
    class Windows implements VariableConfig {
        String   range;
        TimeUnit timeUnit;
    }

    @Data
    class Range implements VariableConfig {
        Date start;
        Date end;
    }

    interface VariableConfig {

    }
}
