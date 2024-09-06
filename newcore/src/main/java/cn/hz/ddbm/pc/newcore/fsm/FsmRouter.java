package cn.hz.ddbm.pc.newcore.fsm;


import cn.hutool.core.lang.Pair;
import cn.hutool.extra.template.engine.freemarker.FreemarkerTemplate;
import cn.hutool.json.JSONUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.chaos.FsmChaosRouter;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.exception.ProcessingException;
import cn.hz.ddbm.pc.newcore.exception.RouterException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.utils.ExpressionEngineUtils;
import lombok.Getter;

import java.util.*;

public class FsmRouter<S extends Enum<S>> implements FsmChaosRouter<S> {
    String noRecordExpression;
    String prcessingExpression;
    @Getter
    protected Map<String, S> stateExpressions;

    public FsmRouter(String noRecordExpression, String prcessingExpression, Map<String, S> stateExpressions) {
        this.noRecordExpression  = noRecordExpression;
        this.prcessingExpression = prcessingExpression;
        this.stateExpressions    = stateExpressions;
    }

    public S router(FsmContext<S> ctx, Object actionResult) throws NoSuchRecordException, ProcessingException {
        String runMode = System.getProperty(Coast.RUN_MODE);
        if (Objects.equals(runMode,Coast.RUN_MODE_CHAOS)) {
            return (S)ProcesorService.chaosHandler().fsmRouter(ctx,this);
        } else {
            for(Map.Entry<String,S> entry:stateExpressions.entrySet()){
                String expression = entry.getKey();
                S state = entry.getValue();
                Map<String, Object> routerContext = new HashMap<>();
                routerContext.put("result", actionResult);
                try {
                    if (ExpressionEngineUtils.eval(expression, routerContext, Boolean.class)) {
                        return state;
                    }
                }catch (Exception e){
                    Logs.error.error("",e);
                }
            }
            throw new RouterException(String.format("无路由结果,%s,%s,%s", ctx.getFlow(), JSONUtil.toJsonStr(actionResult), JSONUtil.toJsonStr(stateExpressions)));
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

    @Override
    public Set<Pair<S,Double>> weights() {
        Set<Pair<S,Double>> weights = new HashSet<>();
        stateExpressions.forEach((expr,s)->{
            try {
                weights.add(Pair.of(s, Double.valueOf(expr)));
            }catch (Exception e){
                weights.add(Pair.of(s, 1.0));
            }
        });
        return weights;
    }
}
