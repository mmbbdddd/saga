package cn.hz.ddbm.pc.core.support;


import java.io.Serializable;

/**
 * @Description TODO
 * @Author wanglin
 * @Date 2024/8/7 22:34
 * @Version 1.0.0
 **/


public interface StatisticsSupport {
    void increment(String flowName, Serializable flowId, Enum node, String key);

    Long get(String flowName, Serializable flowId, Enum node, String key);
}
