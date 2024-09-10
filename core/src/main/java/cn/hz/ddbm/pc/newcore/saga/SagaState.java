package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @param <S> 主状态，
 */
@Getter
public class SagaState<S extends Enum<S>> extends State<S> {
    Integer index;
    @Setter
    Offset offset;

    //    public SagaState(Integer index, Offset offset, SagaFlow<S> flow) {
//        this(index,offset,FlowStatus.RUNNABLE,flow);
//    }
    public SagaState(Integer index, Offset offset, FlowStatus status) {
        this.index  = index;
        this.offset = offset;
        this.status = status;
    }

    @Override
    public Triple<FlowStatus, Integer, Offset> code() {
        return Triple.of(status, index, offset);
    }

    public boolean isForward() {
        return Objects.equals(offset, Offset.task) || Objects.equals(offset, Offset.taskRetry) || Objects.equals(offset, Offset.failover);
    }

    public enum Offset {
        task, taskRetry, failover, rollback, rollbackRetry, rollbackFailover;
    }

    @Override
    public FlowStatus getStatus() {
        if (index == Integer.MAX_VALUE) return FlowStatus.SU;
        if (index == 0) return FlowStatus.FAIL;
        return FlowStatus.RUNNABLE;
    }

    @Override
    public String toString() {
        return "index:" + index + ", offset:" + offset;
    }
}
