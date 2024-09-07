package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.OffsetState;
import cn.hz.ddbm.pc.newcore.State;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @param <M> 主状态，
 */
@Getter
public class SagaState<M extends Enum<M>> extends State<Triple<M, OffsetState, SagaState.Direction>, SagaFlow<M>> {
    M master;
    @Setter
    OffsetState offset;
    @Setter
    Direction   direction;

    private static <M extends Enum<M>> String buildCode(M master, OffsetState offset, Direction direction) {
        if (direction.isForward()) {
            return String.format(">>>>%s:%s", master.name(), offset.name());
        } else {
            return String.format("<<<<%s:%s", master.name(), offset.name());
        }
    }

    public SagaState(M master, OffsetState offset, Direction direction) {
        this.master    = master;
        this.offset    = offset;
        this.direction = direction;
    }


    @Override
    public Triple<M, OffsetState, Direction> stateCode() {
        return Triple.of(master, offset, direction);
    }

    @Override
    public boolean isEnd(SagaFlow<M> flow) {
        return false;
    }

    @Override
    public boolean isPause(SagaFlow<M> flow) {
        return false;
    }

    @Override
    public void setStatus(FlowStatus flowStatus) {

    }


    public SagaState<M> cloneSelf() {
        return new SagaState<>(  this.getMaster(), this.offset, this.direction);
    }

    public SagaState<M> offSet(OffsetState offset) {
        this.offset = offset;
        return this;
    }

    public SagaState<M> status(FlowStatus status) {
        return this;
    }

    public SagaState<M> direction(Direction direction) {
        this.direction = direction;
        return this;
    }



    public enum Direction {
        forward, backoff;


        public boolean isForward() {
            return Objects.equals(this, forward);
        }
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SagaState<?> state1 = (SagaState<?>) object;
        return Objects.equals(master, state1.master) && offset == state1.offset && Objects.equals(direction, state1.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), master, offset, direction);
    }

    @Override
    public String toString() {
        return buildCode(this.master, this.offset, this.direction);
    }
}
