package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;

/**
 * 本地action
 *
 * @param <S>
 */
public interface LocalFsmAction<S extends Enum<S>> extends Action {

    /**
     * 执行业务逻辑
     *
     * @param ctx
     * @throws Exception
     */
    Object localFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws Exception;

}
