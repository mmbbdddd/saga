package cn.hz.ddbm.pc.newcore;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;


public abstract class State<S extends Enum<S>, F> {
    @Getter
    protected S           state;
    @Getter
    protected OffsetState offset;

    public abstract Serializable stateCode();

    public abstract boolean isEnd(F flow);


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        State state = (State) object;
        return Objects.equals(this.stateCode(), state.stateCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(stateCode());
    }

    @Override
    public String toString() {
        return stateCode().toString();
    }


    public abstract State offset(OffsetState offsetState);

    public boolean isPause() {
        return offset.isPaused();
    }
}
