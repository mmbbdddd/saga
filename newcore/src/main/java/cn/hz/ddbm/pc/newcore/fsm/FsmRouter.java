package cn.hz.ddbm.pc.newcore.fsm;


import cn.hutool.json.JSONUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.exception.ProcessingException;
import cn.hz.ddbm.pc.newcore.exception.RouterException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.utils.ExpressionEngineUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FsmRouter<S extends Enum<S>> {
    String noRecordExpression;
    String prcessingExpression;
    @Getter
    protected Map<String, S> stateExpressions;

    public FsmRouter(String noRecordExpression, String prcessingExpression, Map<String, S> stateExpressions) {
        this.noRecordExpression  = noRecordExpression;
        this.prcessingExpression = prcessingExpression;
        this.stateExpressions    = stateExpressions;
    }

    public S router(FsmContext<S> ctx, Object queryResult) throws NoSuchRecordException, ProcessingException {
        String runMode = System.getProperty(Coast.RUN_MODE);
        if (Objects.equals(runMode, Coast.RUN_MODE_CHAOS)) {
            return ProcesorService.chaosHandler().handleRouter(ctx,this);
        } else {
            return stateExpressions.entrySet()
                    .stream()
                    .filter(entry -> {
                        try {
                            String              expression    = entry.getKey();
                            Map<String, Object> routerContext = new HashMap<>();
                            routerContext.put("result", queryResult);
                            return ExpressionEngineUtils.eval(expression, routerContext, Boolean.class);
                        } catch (Exception e) {
                            Logs.error.error("路由错误", e);
                            return false;
                        }
                    })
                    .map(Map.Entry::getValue)
                    .findFirst()
                    .orElseThrow(() -> new RouterException(String.format("路由错误，匹配不到路由结果,%s,%s,%s", ctx.getFlow(), JSONUtil.toJsonStr(queryResult), JSONUtil.toJsonStr(stateExpressions))));
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        FsmRouter<?> fsmRouter = (FsmRouter<?>) object;
        return Objects.equals(noRecordExpression, fsmRouter.noRecordExpression) && Objects.equals(prcessingExpression, fsmRouter.prcessingExpression) && Objects.equals(stateExpressions, fsmRouter.stateExpressions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noRecordExpression, prcessingExpression, stateExpressions);
    }
}
