package cn.hz.ddbm.pc.core;

import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.action.*;
import cn.hz.ddbm.pc.core.action.actiondsl.ParallelAction;
import cn.hz.ddbm.pc.core.action.actiondsl.SerialAction;
import cn.hz.ddbm.pc.core.action.proxy.ChaosActionProxy;
import cn.hz.ddbm.pc.core.action.proxy.CommandActionProxy;
import cn.hz.ddbm.pc.core.action.proxy.QueryActionProxy;
import cn.hz.ddbm.pc.core.action.proxy.SagaActionProxy;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.List;
import java.util.stream.Collectors;

public interface Actions {

    /**
     * 将各种action配置语法转换为特定的Action实现
     */

    public static <T extends Action<S>, S extends Enum<S>> T typeOf(Fsm.Transition<S> t, Class<T> type, Boolean mockBean) {
        if (mockBean) {
            return (T) new ChaosActionProxy<S>(t.getActionDsl());
        }
        if (t.getType().equals(Fsm.TransitionType.SAGA)) {
            return (T) new SagaActionProxy(actionOf(t, SagaAction.class));
        }
        if (t.getType().equals(Fsm.TransitionType.QUERY)) {
            return (T) new QueryActionProxy(actionOf(t, QueryAction.class));
        }
        if (t.getType().equals(Fsm.TransitionType.TO)) {
            return (T) new CommandActionProxy(actionOf(t, CommandAction.class));
        }
        return null;
    }

    /**
     * 将各种action配置语法转换为特定的Action实现
     * <p>
     * 1，单action配置                     ：xxxAction
     * 2，多action串行配置，逗号分隔           ：xxxAction，yyyAction，zzzAction
     * 2，多action并行配置，|分隔             ：xxxAction|yyyAction|zzzAction
     *
     * @param actionDsl
     * @return
     */
    String single_regexp   = "\\w{1,20}";
    String parallel_regexp = "(\\w+,)+\\w+";
    String serial_regexp   = "(\\w+|)+\\w+";

    static <T extends Action<S>, S extends Enum<S>> T actionOf(Fsm.Transition<S> t, Class<T> type) {
        String  actionDsl  = t.getActionDsl();
        Boolean allThrough = null;
        if (actionDsl.matches(single_regexp)) {
            return InfraUtils.getBean(actionDsl, type);
        }
        if (actionDsl.matches(parallel_regexp)) {
            List<Action> actions = StrUtil.split(actionDsl, "|").stream().map(a -> InfraUtils.getBean(a, Action.class)).collect(Collectors.toList());
            return (T) new ParallelAction(allThrough, t, actions);

        }
        if (actionDsl.matches(serial_regexp)) {
            List<Action> actions = StrUtil.split(actionDsl, ",").stream().map(a -> InfraUtils.getBean(a, Action.class)).collect(Collectors.toList());
            return (T) new SerialAction(t, actions);

        }
        throw new RuntimeException(String.format("no such action:%s # %s", actionDsl, type.getSimpleName()));
    }
}
