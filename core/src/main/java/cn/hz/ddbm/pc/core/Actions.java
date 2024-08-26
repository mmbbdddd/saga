package cn.hz.ddbm.pc.core;

import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.decorator.ChaosActionDecorator;
import cn.hz.ddbm.pc.core.action.NoneAction;
import cn.hz.ddbm.pc.core.action.decorator.QueryActionDecorator;
import cn.hz.ddbm.pc.core.action.decorator.SagaActionDecorator;
import cn.hz.ddbm.pc.core.action.decorator.ToActionDecorator;

public interface Actions {

    /**
     * 将各种action配置语法转换为特定的Action实现
     */

    public static <T extends Action<S>, S extends Enum<S>> T of(Fsm.Transition<S> t, Class<T> type, FsmContext<S, ?> ctx) {
        if (null != ctx && ctx.getMockBean()) {
            return (T) new ChaosActionDecorator<S>(t.getActionDsl());
        }
        if (t.getType().equals(Fsm.TransitionType.SAGA)) {
            return (T) new SagaActionDecorator(t.getActionDsl(), t.getFailover(), null);
        }
        if (t.getType().equals(Fsm.TransitionType.QUERY)) {
            return (T) new QueryActionDecorator(t.getActionDsl(), null);
        }
        if (t.getType().equals(Fsm.TransitionType.TO)) {
            return (T) new ToActionDecorator(t.getActionDsl(), null);
        }
        return (T) new NoneAction("");
    }
}
