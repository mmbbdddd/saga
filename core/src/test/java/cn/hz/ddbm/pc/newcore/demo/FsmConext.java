package cn.hz.ddbm.pc.newcore.demo;

import lombok.Data;

@Data
public class FsmConext<S extends Enum<S>> {
    S state ;

    public FsmConext(S state) {
        this.state = state;
    }
}
