package cn.hz.ddbm.pc.core;


import cn.hz.ddbm.pc.core.enums.FlowStatus;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

@Data
public class State {
    Serializable    code;
    FlowStatus.Type type;

    public State(Serializable code) {
        this.code = code;
    }

    public Serializable code() {
        return code;
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
