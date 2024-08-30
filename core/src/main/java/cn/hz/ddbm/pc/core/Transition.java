package cn.hz.ddbm.pc.core;

import lombok.Data;

@Data
public class Transition {
    ProcessorType     type;
    State             from;
    String            event;
    String            actionDsl;
    TransitionHandler handler;


    public Transition(ProcessorType type, State from, String event, String actionDsl, TransitionHandler handler) {
        this.type      = type;
        this.from      = from;
        this.event     = event;
        this.actionDsl = actionDsl;
        this.handler   = handler;
    }
}
