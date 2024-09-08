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
    SagaFlow<S> flow;

    public SagaState(Integer index, Offset offset, SagaFlow<S> flow) {
        this.index  = index;
        this.offset = offset;
        this.flow   = flow;
    }

    @Override
    public Triple<FlowStatus,Integer,Offset> code() {
        return Triple.of(status,index,offset);
    }

    public enum Offset {
        task, taskRetry, failover, rollback, rollbackRetry, rollbackFailover;

        public Boolean isForward() {
            return Objects.equals(task, this) || Objects.equals(taskRetry, this) || Objects.equals(failover, this);
        }
    }
}
