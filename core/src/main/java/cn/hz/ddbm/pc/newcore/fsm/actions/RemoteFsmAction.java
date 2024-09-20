package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;

/**
 * Fsm业务逻辑
 *
 * @param <S> 主状态
 */
public interface RemoteFsmAction  extends FsmAction {

    /**
     * 执行业务逻辑
     *
     * @param ctx
     * @throws Exception
     */
    void remoteFsm(FlowContext<FsmState > ctx) throws Exception;

    /**
     * 执行结果查询
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    Object remoteFsmQuery(FlowContext<FsmState > ctx) throws Exception;


}
