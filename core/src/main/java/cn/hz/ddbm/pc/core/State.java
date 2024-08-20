package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.enums.FlowStatus;
import lombok.Data;

import java.util.Objects;

@Data
public class State<S extends Enum<S>> {
    S          state;
    FlowStatus status;

    public State(S state, FlowStatus type) {
        this.state  = state;
        this.status = type;
    }

    public boolean isRunnable() {
        return (Objects.equals(status, FlowStatus.RUNNABLE) || Objects.equals(status, FlowStatus.INIT));
    }


    public void flush(String event, S target, Fsm<S> fsm) {
        this.status = fsm.getNode(target).getType();
        this.state  = target;
        //如果是流程指令，则执行流程状态变迁
        if (FlowStatus.Event.isFlowEvent(event)) {
            this.status = this.status.on(FlowStatus.Event.valueOf(event.toUpperCase()));
        }
    }

    public void update(S state) {
        this.state = state;
    }
}
