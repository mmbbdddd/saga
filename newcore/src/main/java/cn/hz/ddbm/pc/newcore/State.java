package cn.hz.ddbm.pc.newcore;

import java.io.Serializable;
import java.util.Objects;


public abstract class State<S extends Serializable> {
    protected S code;

    public State(S code) {
        this.code = code;
    }

    public abstract S code();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        State state = (State) object;
        return Objects.equals(this.code, state.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
