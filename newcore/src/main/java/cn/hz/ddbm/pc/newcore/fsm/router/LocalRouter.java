package cn.hz.ddbm.pc.newcore.fsm.router;

import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地方法调用结果路由，不需要中间过程记录，适用于本机方法调用。
 *
 * @param <S>
 */
public class LocalRouter<S extends Enum<S>> extends FsmRouter<S> {
    public LocalRouter(S to) {
        super("false", "false", new HashMap<String, S>() {{
            this.put("true", to);
        }});
    }

}
