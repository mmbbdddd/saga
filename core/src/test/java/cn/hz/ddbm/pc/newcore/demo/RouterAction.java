package cn.hz.ddbm.pc.newcore.demo;

public interface RouterAction<S extends Enum<S>> extends Action<S> {
    default S executeTo(FsmConext<S> ctx){
        Object result = execute(ctx);
        return route(result,ctx);
    }

    Object execute(FsmConext<S> ctx);

    S route(Object result,FsmConext<S> ctx);
}
