package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * Fsm业务逻辑
 * @param <S>  主状态
 */
public interface RemoteFsmAction<S extends Enum<S>> extends Action {

    /**
     * 执行业务逻辑
     * @param ctx
     * @throws Exception
     */
    void execute(FsmContext<S> ctx) throws Exception;

    /**
     * 执行结果查询
     * @param ctx
     * @return
     * @throws Exception
     */
    Object executeQuery(FsmContext<S> ctx) throws Exception;

}
