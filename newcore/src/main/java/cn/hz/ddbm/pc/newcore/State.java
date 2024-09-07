package cn.hz.ddbm.pc.newcore;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;


public abstract class State<S extends Serializable> {
    @Getter
    @Setter
    protected FlowStatus status;

    public State(FlowStatus status) {
        this.status = status;
    }

    public abstract S code();

    public boolean isEnd() {
        return FlowStatus.isEnd(status);
    }

    public boolean isPause() {
        return FlowStatus.isPause(status);
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
