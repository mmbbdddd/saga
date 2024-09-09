package cn.hz.ddbm.pc.newcore.demo;

public interface Action<S extends Enum<S>> {
    S executeTo(FsmConext<S> ctx);
}
