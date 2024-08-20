package cn.hz.ddbm.pc.core.utils;

import cn.hutool.extra.expression.ExpressionUtil;

import java.util.Map;

public class ExpressionEngineUtils {
    public static <T> T eval(String expression, Map<String, Object> ctx, Class<T> resultType) {
        return (T) ExpressionUtil.eval(expression, ctx);
    }
}
