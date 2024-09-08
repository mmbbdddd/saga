package cn.hz.ddbm.pc.newcore;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;

@Data
public abstract class State<E extends Enum<E>> implements Serializable {
    FlowStatus status;

    public abstract E code();

    public boolean isPaused() {
        return FlowStatus.isPause(getStatus());
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
