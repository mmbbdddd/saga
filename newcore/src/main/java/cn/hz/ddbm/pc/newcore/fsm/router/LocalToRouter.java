package cn.hz.ddbm.pc.newcore.fsm.router;

import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;

import java.util.HashMap;

/**
 * 本地方法调用结果路由，不需要中间过程记录，适用于本机方法调用。
 *
 * @param <S>
 */
public class LocalToRouter<S extends Enum<S>> extends LocalRouter<S> {
    public LocalToRouter(S to) {
        super(new HashMap<String, S>() {{
            this.put("true", to);
        }});
    }

}
