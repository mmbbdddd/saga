package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * Fsm业务逻辑
 *
 * @param <S> 主状态
 */
public interface RemoteFsmAction<S extends Enum<S>> extends FsmAction {

    /**
     * 执行业务逻辑
     *
     * @param ctx
     * @throws Exception
     */
    void remoteFsm(FsmContext<S> ctx) throws Exception;

    /**
     * 执行结果查询
     *
     * @param ctx
     * @return
     * @throws Exception
     */
    Object remoteFsmQuery(FsmContext<S> ctx) throws Exception;


}
