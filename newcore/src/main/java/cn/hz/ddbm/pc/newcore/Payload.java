package cn.hz.ddbm.pc.newcore;

import java.io.Serializable;

public interface Payload<S> {

    Serializable getId();

    FlowStatus getStatus();

    void setStatus(FlowStatus status);

    S getState();

    void setState(S state);
}
