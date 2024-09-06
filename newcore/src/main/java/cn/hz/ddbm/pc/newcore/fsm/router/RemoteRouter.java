package cn.hz.ddbm.pc.newcore.fsm.router;

import cn.hz.ddbm.pc.newcore.fsm.FsmRouter;

import java.util.Map;

/**
 * 直接从A状态==B状态,不存在中间过程记录和处理过程：适用于本机方法调用
 *
 * todo 当使用此router时候，检测action是否有外部io发生，提醒，告警。
 *
 * @param <S>
 */
public class RemoteRouter<S extends Enum<S>> extends FsmRouter<S> {
    public RemoteRouter(String noRecordExpression,String processingExpression,Map<String, S> stateExpression) {
        super(noRecordExpression, processingExpression, stateExpression);
    }
}
