package cn.hz.ddbm.pc.status.dao;


import cn.hz.ddbm.pc.newcore.Payload;
import cn.hz.ddbm.pc.newcore.fsm.FsmPayload;

public interface PayloadDao<T extends Payload> {
    String flowName();

    void save(FsmPayload data);

    FsmPayload get(String flow);
}
