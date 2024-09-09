package cn.hz.ddbm.pc.newcore;

import java.io.Serializable;

public interface Payload<S extends State> {
    Serializable getId();

    S getState();

    void setState(S state);

    FlowStatus getStatus();

    void setStatus(FlowStatus state);


}
