package cn.hz.ddbm.pc.newcore;

import java.util.Objects;

/**
 * 插件:
 * ___1,实现监控、统计等功能和业务功能分离。
 * ___2,如果业务和插件之间需要信息交互，通过上下文完成。
 */

public abstract class Plugin<S extends State> {
    public abstract String code();

    public abstract void preAction(FlowContext<S> ctx);

    public abstract void postAction(S preState, FlowContext<S> ctx);

    public abstract void errorAction(S preState, Exception e, FlowContext<S> ctx);

    public abstract void finallyAction(S preNode, FlowContext<S> ctx);

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Plugin plugin = (Plugin) object;
        return Objects.equals(code(), plugin.code());
    }

    @Override
    public int hashCode() {
        return Objects.hash(code());
    }
}
