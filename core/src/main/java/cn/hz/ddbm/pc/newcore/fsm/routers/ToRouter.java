package cn.hz.ddbm.pc.newcore.fsm.routers;




import cn.hz.ddbm.pc.newcore.fsm.Router;

import java.util.HashMap;

/**
 * 本地方法调用结果路由，不需要中间过程记录，适用于本机方法调用。
 *
 * @param <S>
 */
public class ToRouter<S extends Enum<S>> extends Router<S> {
    public ToRouter(S to) {
        super(new HashMap<String, S>() {{
            this.put("true", to);
        }});
    }

}
