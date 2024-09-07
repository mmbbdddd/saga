package cn.hz.ddbm.pc.newcore.fsm.router;

import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 本地方法调用结果路由，不需要中间过程记录，适用于本机方法调用。
 *
 * @param <S>
 */
public class LocalRouter<S extends Enum<S>> extends FsmRouter<S> {
    public LocalRouter(Map<String, S> stateExpressions) {
        super("false", "false",stateExpressions);
    }


    public LocalRouter(Table<String, S, Double> chaos) {
        super("false", "false",chaos);
    }

}
