package cn.hz.ddbm.pc.newcore.fsm;


import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.json.JSONUtil;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.utils.EnvUtils;
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

    public Enum router(FlowContext<FsmState> ctx, Object actionResult) {
        if (EnvUtils.isChaos()) {
            return routerByWeight();
        } else {
            Map<String, Object> routerContext = new HashMap<>();
            routerContext.put("result", actionResult);
            for (Table.Cell<String, S, Double> entry : stateExpressions) {
                String expression = entry.getRowKey();
                Enum   state      = entry.getColumnKey();
                if (ExpressionEngineUtils.eval(expression, routerContext, Boolean.class)) {
                    return state;
                }
            }
            return null;
        }
    }


    @Override
    public String toString() {
        return JSONUtil.toJsonStr(stateExpressions.values());
    }

    protected static <S> Table<String, S, Double> parseToTable(Map<String, S> stateExpressions) {
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
