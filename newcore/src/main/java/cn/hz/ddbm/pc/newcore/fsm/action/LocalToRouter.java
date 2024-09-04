package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.ProcessingException;

import java.util.HashMap;

/**
 * 直接从A状态==B状态,不存在中间过程记录和处理过程
 *
 * @param <S>
 */
public class LocalToRouter<S extends Enum<S>> extends LocalRouter<S> {
    public LocalToRouter(S to) {
        super(new HashMap<String, S>() {{
            this.put("true", to);
        }});
    }

    public S router(FsmContext<S> ctx, Object queryResult) throws NoSuchRecordException, ProcessingException {
        return stateExpressions.values().stream().findFirst().get();
    }
}
