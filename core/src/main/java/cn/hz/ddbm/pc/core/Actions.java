package cn.hz.ddbm.pc.core;

import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.action.impl.ChaosAction;
import cn.hz.ddbm.pc.core.action.impl.NoneAction;
import cn.hz.ddbm.pc.core.action.impl.ParallelAction;
import cn.hz.ddbm.pc.core.action.impl.SerialAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Actions {

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

    public static <T extends Action<S>, S extends Enum<S>> T of(Fsm.Transition<S> t, Class<T> type, FsmContext<S, ?> ctx) {
        if (null != ctx && ctx.getMockBean()) {
            if (StrUtil.isBlank(t.getActionDsl())) {
                return (T) new NoneAction("");
            } else {
                return (T) new ChaosAction<S>(t.getActionDsl());
            }
        }
        if (StrUtil.isBlank(t.getActionDsl())) {
            return (T) new NoneAction("");
        }
        if (t.getActionDsl().matches(single_regexp)) {
            return InfraUtils.getBean(t.getActionDsl(), type);
        }
        if (t.getActionDsl().matches(parallel_regexp)) {
            String[] actionBeanNames = t.getActionDsl().split(",");
            List<SagaAction> actions = Arrays.stream(actionBeanNames)
                    .map(name -> InfraUtils.getBean(name, SagaAction.class))
                    .collect(Collectors.toList());
            return (T) new ParallelAction(t.getActionDsl(), null, null, t.getFailover(), actions, null);
        }
        if (t.getActionDsl().matches(serial_regexp)) {
            String[] actionBeanNames = t.getActionDsl().split(",");
            List<Action> actions = Arrays.stream(actionBeanNames)
                    .map(name -> InfraUtils.getBean(name, Action.class))
                    .collect(Collectors.toList());
            return (T) new SerialAction(t.getActionDsl(), t.getFailover(), actions);
        }
        return (T) new NoneAction("");
    }
}
