package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.coast.Coasts;
import lombok.Data;

@Data
public class Event {
    Type   type;
    String code;

    public Event(Type type, String code) {
        this.type = type;
        this.code = code;
    }

    public static Event of(String event) {
        Type type;
        if (event.equalsIgnoreCase(Coasts.EVENT_CANCEL) || event.equalsIgnoreCase(Coasts.EVENT_PAUSE)) {
            type = Type.FLOW_EVENT;
        } else {
            type = Type.NODE_EVENT;
        }
        return new Event(type, event);
    }

    public static Event of(Type type, String event) {
        return new Event(type, event);
    }


    public enum Type {
        //来自外部输入的指令
        FLOW_EVENT,
        NODE_EVENT
    }
}
