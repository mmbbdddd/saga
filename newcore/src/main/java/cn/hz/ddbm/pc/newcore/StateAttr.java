package cn.hz.ddbm.pc.newcore;

import lombok.Data;

@Data
public class StateAttr {
    Integer retry;

    public static StateAttr defaultOf() {
        StateAttr def = new StateAttr();
        def.setRetry(10);
        return def;
    }
}
