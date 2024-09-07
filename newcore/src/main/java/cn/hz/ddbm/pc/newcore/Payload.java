package cn.hz.ddbm.pc.newcore;

import java.io.Serializable;

public interface Payload<S, F extends FlowModel> {

    Serializable getId();

    S getState();

    void setState(S state);

    FlowStatus getStatus(F flow);
}
