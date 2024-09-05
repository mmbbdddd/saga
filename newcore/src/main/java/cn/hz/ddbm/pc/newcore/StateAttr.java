package cn.hz.ddbm.pc.newcore;

import lombok.Data;

@Data
public class StateAttr {
    Integer retry;
    public static StateAttr defaultOf() {
        return null;
    }
}
