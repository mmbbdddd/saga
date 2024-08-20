package cn.hz.ddbm.pc.status.dao;

import cn.hz.ddbm.pc.core.FsmPayload;

public interface PayloadDao<T extends FsmPayload> {
    String flowName();

    void save(FsmPayload data);

    FsmPayload get(String flow);
}
