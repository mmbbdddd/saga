package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Payload;
import lombok.Data;

import java.io.Serializable;

@Data
public class FsmPayload implements Payload<FsmState> {
    Serializable id;
    FlowStatus   status;
    FsmState     state;

    @Override
    public Serializable getId() {
        return id;
    }


}
