package cn.hz.ddbm.pc.newcore;

import java.io.Serializable;

public interface Payload<S> {

    Serializable getId();

    S getState();

    void setState(S state);
}
