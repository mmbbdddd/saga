package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import lombok.Data;

@Data
public class StateAttr {
    Integer retry;

    public static StateAttr defaultOf() {
        StateAttr def = new StateAttr();
        def.setRetry(Coast.DEFAULT_RETRYTIME);
        return def;
    }
}
