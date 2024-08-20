package cn.hz.ddbm.pc.profile.chaos;

import cn.hutool.core.util.ReflectUtil;
import cn.hz.ddbm.pc.core.utils.ExpressionEngineUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 混沌规则表
 */
public class ChaosRule {
    ChaosTarget                      target;
    /**
     * 触发条件，el表达式。
     * 表达式的值有三类
     * obj：执行对象
     * method：方法
     * 参数：分别以args1，args2，args3.。。命名
     */
    String                           expression;
    String                           errorMessage;
    //触发概率
    double                           probability;
    //触发后，跑出的异常类型（随便选一个）
    List<Class<? extends Throwable>> errorTypes;

    Throwable exception = null;

    public ChaosRule(ChaosTarget target, String expression, String errorMessage, double probability, List<Class<? extends Throwable>> errorTypes) {
        this.target       = target;
        this.expression   = expression;
        this.errorMessage = errorMessage;
        this.probability  = probability;
        this.errorTypes   = errorTypes;
        int choice = Double.valueOf(Math.random() * errorTypes.size()).intValue();

        try {
            this.exception = errorTypes.get(choice).newInstance();
            ReflectUtil.setFieldValue(exception, "detailMessage", errorMessage + ":" + Math.random());
            ReflectUtil.setFieldValue(exception, "stackTrace", null);
            ReflectUtil.setFieldValue(exception, "cause", null);
        } catch (Exception e) {
        }
    }

    public boolean match(ChaosTarget target, Object proxy, Method method, Object[] args) {
        if (!Objects.equals(target, this.target)) {
            return false;
        }
        Map<String, Object> ctx = new HashMap<>();
        ctx.put("obj", proxy);
        ctx.put("method", method.getName());
        for (int idx = 0; idx < args.length; idx++) {
            ctx.put("arg" + (idx + 1), args[idx]);
        }
        return ExpressionEngineUtils.eval(expression, ctx, Boolean.class);
    }

    public void raiseException() throws Throwable {
        if (null != exception) {
//        stackTrace
            throw exception;
        }
    }

    public boolean probabilityIsTrue() {
        return Math.random() < probability;
    }
}
