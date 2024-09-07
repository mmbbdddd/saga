package cn.hz.ddbm.pc.newcore;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;


public abstract class State<S extends Enum<S>, F> implements Serializable {
    @Getter
    protected S           state;
    @Getter
    protected OffsetState offset;

    public abstract Serializable code();

    public abstract boolean isEnd(F flow);


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        State state = (State) object;
        return Objects.equals(this.code(), state.code());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code());
    }

    @Override
    public String toString() {
        return code().toString();
    }


    public abstract State offset(OffsetState offsetState);

    public boolean isPause() {
        return offset.isPaused();
    }
}
