package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;

/**
 * 本地action
 *
 * @param <S>
 */
public interface LocalFsmAction extends FsmAction {

    /**
     * 执行业务逻辑
     *
     * @param ctx
     * @throws Exception
     */
    Object doLocalFsm(FlowContext<FsmState> ctx) throws Exception;

}
