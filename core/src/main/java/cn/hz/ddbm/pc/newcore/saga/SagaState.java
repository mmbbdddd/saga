package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Data;

import java.io.Serializable;

@Data
public class SagaState implements State {
    public FlowStatus        flowStatus;
    public Integer           index;
    public SagaWorker.Offset offset;

    @Override
    public Serializable code() {
        return null;
    }
}
