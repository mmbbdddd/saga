package cn.hz.ddbm.pc.core.processor.saga;

import cn.hz.ddbm.pc.core.State;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SagaState<S> extends State {
    S      state;
    Offset offset;

    public static <S> SagaState<S> of(S s) {
        return new SagaState<>(s);
    }


    public SagaState(S state) {
        super(state.toString());
        this.state  = state;
        this.offset = Offset.task;
    }

    public SagaState(S state, Offset offset) {
        super(state.toString());
        this.state  = state;
        this.offset = offset;
    }


    @Override
    public Serializable code() {
        return String.format("%s_%s", state, offset);
    }


    public enum Offset {
        task, failover, su, rollback, rollbackFailover;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        SagaState<S> sagaState = (SagaState<S>) object;
        return Objects.equals(state, sagaState.state) && offset == sagaState.offset;
    }

    @Override
    public int hashCode() {
        return Objects.hash(state, offset);
    }

    public List<SagaState> subStates() {
        return EnumSet.allOf(Offset.class).stream().map(os -> new SagaState(this, os)).collect(Collectors.toList());
    }

    public SagaState<S> task() {
        return new SagaState<>(this.state, Offset.task);
    }

    public SagaState<S> failover() {
        return new SagaState<>(this.state, Offset.failover);
    }

    public SagaState<S> su() {
        return new SagaState<>(this.state, Offset.su);
    }

    public SagaState<S> rollback() {
        return new SagaState<>(this.state, Offset.rollback);
    }

    public SagaState<S> rollbackFailover() {
        return new SagaState<>(this.state, Offset.rollbackFailover);
    }
}
