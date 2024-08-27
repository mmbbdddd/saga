package cn.hz.ddbm.pc.core;


import cn.hz.ddbm.pc.core.enums.FlowStatus;

import java.io.Serializable;

/**
 * @Description
 * @Author wanglin
 * @Date 2024/8/7 21:59
 * @Version 1.0.0
 **/


public interface FsmPayload<S extends Enum<S>> {
    Serializable getId();

    FlowStatus getStatus();

    S getState();

    void setStatusSate(FlowStatus status, S state);
}
