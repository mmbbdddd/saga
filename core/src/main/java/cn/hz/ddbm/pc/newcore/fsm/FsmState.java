package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Data;

import java.io.Serializable;

@Data
public class FsmState<S extends Enum<S>> implements State {
    public FlowStatus       flowStatus;
    public S                state;
    public FsmWorker.Offset offset;

    @Override
    public Serializable code() {
        return null;
    }



}
