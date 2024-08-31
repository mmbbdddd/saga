package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.State;
import cn.hz.ddbm.pc.newcore.config.Coast;
import lombok.Getter;

import java.util.Objects;

@Getter
public class SagaState<S> extends State {
    S       state;
    Offset  offset;
    Boolean isForward;


    public SagaState(S state, Offset offset, Boolean isForward) {
        super(state.toString());
        this.state     = state;
        this.offset    = offset;
        this.isForward = isForward;
    }

    @Override
    public String code() {
        return String.format("%s_%s_%s", state, offset, isForward ? Coast.SAGA.EVENT_FORWARD : Coast.SAGA.EVENT_BACKOFF);
    }

    public SagaState<S> cloneSelf() {
        return new SagaState<>(this.getState(), this.offset, this.isForward);
    }


    public enum Offset {
        task, failover, su, retry, fail;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        SagaState<?> state1 = (SagaState<?>) object;
        return Objects.equals(state, state1.state) && offset == state1.offset && Objects.equals(isForward, state1.isForward);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), state, offset, isForward);
    }

    @Override
    public String toString() {
        return state + "@" + offset;
    }
}
