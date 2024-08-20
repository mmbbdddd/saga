package cn.hz.ddbm.pc.core;

import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.core.action.MultiAction;
import cn.hz.ddbm.pc.core.action.NoneAction;
import cn.hz.ddbm.pc.core.utils.InfraUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public interface Action<S extends Enum<S>> {
    String beanName();


    void execute(FsmContext<S, ?> ctx) throws Exception;

    interface QueryAction<S extends Enum<S>> extends Action<S> {
        S query(FsmContext<S, ?> ctx) throws Exception;
    }

    interface SagaAction<S extends Enum<S>> extends Action<S>, QueryAction<S> {

    }

    /**
     * 将各种action配置语法转换为特定的Action实现
     * <p>
     * 1，单action配置                     ：xxxAction
     * 2，多action配置，逗号分隔             ：xxxAction，yyyAction，zzzAction
     * 3，sagaAction配置                   ：xxxAction,（failover）  or  xxxAction,yyyyAction,zzzAction,(failover)
     * 4，noneAction配置                   ：“”   or “none”
     *
     * @param actionDsl
     * @return
     */
    String single_regexp = "\\w{1,20}";
    String multi_regexp  = "(\\w+,)+\\w+";

    public static <T extends Action<S>, S extends Enum<S>> T of(String actionDsl, Class<T> type, FsmContext<S, ?> ctx) {
        if (null != ctx && ctx.getMockBean()) {
            if (StrUtil.isBlank(actionDsl)) {
                return (T) new NoneAction("");
            } else {
                return InfraUtils.getBean("chaosAction", type);
            }
        }
        if (StrUtil.isBlank(actionDsl)) {
            return (T) new NoneAction("");
        }
        if (actionDsl.matches(single_regexp)) {
            return InfraUtils.getBean(actionDsl, type);
        }
        if (actionDsl.matches(multi_regexp)) {
            String[] actionBeanNames = actionDsl.split(",");
            List<Action> actions = Arrays.stream(actionBeanNames)
                    .map(name -> InfraUtils.getBean(name, Action.class))
                    .collect(Collectors.toList());
            return (T) new MultiAction(actionDsl, actions);
        }
        return (T) new NoneAction("");
    }


}
