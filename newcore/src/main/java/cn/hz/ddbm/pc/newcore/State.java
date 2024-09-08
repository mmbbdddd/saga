package cn.hz.ddbm.pc.newcore;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public abstract class State<S extends Enum<S>> implements Serializable {
    protected FlowStatus status;

    public abstract Serializable code();

    public boolean isPaused() {
        return FlowStatus.isPaused(getStatus());
    }

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


}
