package cn.hz.ddbm.pc.core.utils;

import cn.hutool.extra.expression.ExpressionUtil;
import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Map;

public class ExpressionEngineUtils {
    private static final ExpressionParser parser = new SpelExpressionParser();

    public static <T> T eval(String expression, Map<String, Object> context, Class<T> type) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.getPropertyAccessors().add(new MapAccessor());
        context.forEach(evaluationContext::setVariable);
        return parser.parseExpression(expression).getValue(evaluationContext, type);
    }
}
