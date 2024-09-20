package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Data;

import java.io.Serializable;

@Data
public class FsmState implements State {
    public FlowStatus       flowStatus;
    public Enum             state;
    public FsmWorker.Offset offset;

    @Override
    public Serializable code() {
        return null;
    }


}
