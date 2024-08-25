package cn.hz.ddbm.pc.core;


import java.io.Serializable;

/**
 * @Description
 * @Author wanglin
 * @Date 2024/8/7 21:59
 * @Version 1.0.0
 **/


public interface FsmPayload<S extends Enum<S>> {
    Serializable getId();

    State<S> getStatus();

    void setStatus(State<S> status);
}
