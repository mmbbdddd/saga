package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.enums.FlowStatus;
import lombok.Data;

import java.util.Objects;

@Data
public class State<S extends Enum<S>> {
    S          name;
    FlowStatus type;

    public State(S state, FlowStatus type) {
        this.name = state;
        this.type = type;
    }

    public boolean isRunnable() {
        return (Objects.equals(type, FlowStatus.RUNNABLE) || Objects.equals(type, FlowStatus.INIT));
    }


    public void flush(String event, S target, Fsm<S> fsm) {
        this.type = fsm.getNode(target).getType();
        this.name = target;
        //如果是流程指令，则执行流程状态变迁
        if (FlowStatus.Event.isFlowEvent(event)) {
            this.type = this.type.on(FlowStatus.Event.valueOf(event.toUpperCase()));
        }
    }

    public void update(S state) {
        this.name = state;
    }
}
