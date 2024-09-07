package cn.hz.ddbm.pc.newcore;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;


public abstract class State<S extends Serializable, F extends FlowModel> {

    public abstract S stateCode();

    public abstract boolean isEnd(F flow);

    public abstract boolean isPause(F flow);

    public abstract void setStatus(FlowStatus flowStatus);

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


}
