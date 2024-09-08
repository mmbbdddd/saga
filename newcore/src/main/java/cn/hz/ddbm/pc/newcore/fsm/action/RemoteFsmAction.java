package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;

/**
 * Fsm业务逻辑
 *
 * @param <S> 主状态
 */
public interface RemoteFsmAction<S extends Enum<S>> extends Action {

    /**
     * 执行业务逻辑
     *
     * @param ctx
     * @throws Exception
     */
    void remoteFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception;

    /**
     * 执行结果查询
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    Object remoteFsmQuery(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception;


}
