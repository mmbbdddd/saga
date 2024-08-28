package cn.hz.ddbm.pc.core;

import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;
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

    public static <T extends Action<S>, S extends Enum<S>> T typeOf(Transition<S> t, Class<T> type, Boolean mockBean) {
        if (mockBean) {
            return (T) new ChaosActionProxy<S>(t.getActionDsl());
        }
        if (t.getType().equals(Transition.Type.SAGA)) {
            return (T) new SagaActionProxy(actionDsl(t, SagaAction.class));
        }
        if (t.getType().equals(Transition.Type.QUERY)) {
            return (T) new QueryActionProxy(actionDsl(t, QueryAction.class));
        }
        if (t.getType().equals(Transition.Type.TO)) {
            return (T) new CommandActionProxy(actionDsl(t, CommandAction.class));
        }
        return null;
    }

    /**
     * 将各种action配置语法转换为特定的Action实现
     * <p>
     * 1，单action配置    xxxAction
     * 2，a1|a2|a3       并行，any
     * 3，a1&a2&a3       并行，all
     * 4，a1，a2，a3，    串行，all
     *
     * @param actionDsl
     * @return
     */
    String single_regexp       = "\\w+";
    String parallel_any_regexp = "(\\w+|)+\\w+";
    String parallel_all_regexp = "(\\w+\\&)+\\w+";
    String serial_regexp       = "(\\w+,)+\\w+";

    static <T extends Action<S>, S extends Enum<S>> T actionDsl(Transition<S> t, Class<T> type) {
        String actionDsl = t.getActionDsl();
        if (actionDsl.matches(single_regexp)) {
            return InfraUtils.getBean(actionDsl, type);
        }
//        没必要搞这么复杂。暂不支持fork join 模式
//        if (actionDsl.matches(parallel_any_regexp)) {
//            List<Action> actions = StrUtil.split(actionDsl, "|").stream().map(a -> InfraUtils.getBean(a, Action.class)).collect(Collectors.toList());
//            return (T) new ParallelAction(false, t, actions);
//        }
//        if (actionDsl.matches(parallel_all_regexp)) {
//            List<Action> actions = StrUtil.split(actionDsl, "&").stream().map(a -> InfraUtils.getBean(a, Action.class)).collect(Collectors.toList());
//            return (T) new ParallelAction(true, t, actions);
//        }
        if (actionDsl.matches(serial_regexp)) {
            List<Action> actions = StrUtil.split(actionDsl, ",").stream().map(a -> InfraUtils.getBean(a, Action.class)).collect(Collectors.toList());
            return (T) new SerialAction(t, actions);

        }
        throw new RuntimeException(String.format("no such action:%s # %s", actionDsl, type.getSimpleName()));
    }
}
