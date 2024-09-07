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
public class SagaState<M extends Enum<M>> extends State<M, SagaFlow<M>> {
    @Setter
    Direction   direction;
    public SagaState(M master, OffsetState offset, Direction direction) {
        this.state    = master;
        this.offset    = offset;
        this.direction = direction;
    }
    private static <M extends Enum<M>> String buildCode(M master, OffsetState offset, Direction direction) {
        if (direction.isForward()) {
            return String.format(">>>>%s:%s", master.name(), offset.name());
        } else {
            return String.format("<<<<%s:%s", master.name(), offset.name());
        }
    }

    @Override
    public Triple<M, OffsetState, Direction> stateCode() {
        return Triple.of(state, offset, direction);
    }

    @Override
    public boolean isEnd(SagaFlow<M> flow) {
        return flow.isEnd(this);
    }



    public SagaState<M> cloneSelf() {
        return new SagaState<>(this.getState(), this.offset, this.direction);
    }


    @Override
    public SagaState<M> offset(OffsetState offset) {
        this.offset = offset;
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
        return Objects.equals(state, state1.state) && offset == state1.offset && Objects.equals(direction, state1.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, offset, direction);
    }

    @Override
    public String toString() {
        return buildCode(this.state, this.offset, this.direction);
    }


}
