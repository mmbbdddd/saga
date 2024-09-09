package cn.hz.ddbm.pc.newcore.fsm;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.json.JSONUtil;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.exception.ProcessingException;
import cn.hz.ddbm.pc.newcore.exception.RouterException;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.utils.ExpressionEngineUtils;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;
import lombok.Getter;

import java.util.*;

public class Router<S extends Enum<S>> {
    @Getter
    protected Table<String, S, Double> stateExpressions;


    public Router(Map<String, S> stateExpressions) {
        this(parseToTable(stateExpressions));
    }

    public Router(Table<String, S, Double> stateExpressions) {
        this.stateExpressions = stateExpressions;
    }

    public S router(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx, Object actionResult) throws NoSuchRecordException, ProcessingException {
        String runMode = System.getProperty(Coast.RUN_MODE);
        if (Objects.equals(runMode, Coast.RUN_MODE_CHAOS)) {
            return routerByWeight();
        } else {
            Map<String, Object> routerContext = new HashMap<>();
            routerContext.put("result", actionResult);
            for (Table.Cell<String, S, Double> entry : stateExpressions) {
                String expression = entry.getRowKey();
                S      state      = entry.getColumnKey();
                try {
                    if (ExpressionEngineUtils.eval(expression, routerContext, Boolean.class)) {
                        return state;
                    }
                } catch (Exception e) {
                    Logs.error.error("", e);
                }
            }
            throw new RouterException(String.format("无路由结果,%s,%s,%s", ctx.getFlow(), JSONUtil.toJsonStr(actionResult), JSONUtil.toJsonStr(stateExpressions)));
        }
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonStr(stateExpressions.values());
    }

    protected static <S extends Enum<S>> Table<String, S, Double> parseToTable(Map<String, S> stateExpressions) {
        Table<String, S, Double> table = new RowKeyTable<>();
        stateExpressions.forEach((expr, state) -> {
            table.put(expr, state, 1.0);
        });
        return table;
    }


    private S routerByWeight() {
        Set<Pair<S, Double>> weights = new HashSet<>();
        stateExpressions.forEach(c -> {
            weights.add(Pair.of(c.getColumnKey(), c.getValue()));
        });
        return RandomUitl.selectByWeight(Math.random() + "", weights);
    }
}
