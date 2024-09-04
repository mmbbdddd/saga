package cn.hz.ddbm.pc.newcore.fsm.action;

import java.util.HashMap;

/**
 * 直接从A状态==B状态,不存在中间过程记录和处理过程
 *
 * @param <S>
 */
public class LocalToRouter<S> extends LocalRouter<S> {
    public LocalToRouter(S to) {
        super(new HashMap<String, S>() {{
            this.put("true", to);
        }});
    }
}
