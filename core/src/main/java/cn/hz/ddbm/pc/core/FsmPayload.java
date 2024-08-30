package cn.hz.ddbm.pc.core;


import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.enums.FlowStatus;

import java.io.Serializable;

/**
 * @Description
 * @Author wanglin
 * @Date 2024/8/7 21:59
 * @Version 1.0.0
 **/


public interface FsmPayload<S extends State> {

    Serializable getId();

    Triple<FlowStatus,S,String> getStatus();

    void setStatus(Triple<FlowStatus,S,String> status);
}
