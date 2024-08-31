package cn.hz.ddbm.pc.newcore;

import lombok.Data;

import java.io.Serializable;
import java.util.Objects;


public class State {
    Serializable code;

    public State(Serializable code) {
        this.code = code;
    }

    public String code() {
        return code.toString();
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        State state = (State) object;
        return Objects.equals(code, state.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
