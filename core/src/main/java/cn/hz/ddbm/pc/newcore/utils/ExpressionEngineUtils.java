package cn.hz.ddbm.pc.newcore.utils;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * El引擎。动静分离
 */
public class ExpressionEngineUtils {
    private static final ExpressionParser parser = new SpelExpressionParser();

    public static Object eval(String expression) {
        return eval(expression, new HashMap<>());
    }

    public static Object eval(String expression, Map<String, Object> context) {
        EvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.getPropertyAccessors().add(new MapAccessor());
        context.forEach(evaluationContext::setVariable);
        return parser.parseExpression(expression).getValue(evaluationContext);
    }

    public static <T> T eval(String expression, Map<String, Object> context, Class<T> type) {
        EvaluationContext evaluationContext = new StandardEvaluationContext(context);
        evaluationContext.getPropertyAccessors().add(new MapAccessor());
        context.forEach(evaluationContext::setVariable);
        return parser.parseExpression(expression).getValue(evaluationContext, type);
    }
}
