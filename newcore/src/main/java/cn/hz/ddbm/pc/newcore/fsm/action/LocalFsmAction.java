package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * 本地action
 * @param <S>
 */
public interface LocalFsmAction<S extends Enum<S>> extends Action {

    /**
     * 执行业务逻辑
     * @param ctx
     * @throws Exception
     */
    Object execute(FsmContext<S> ctx) throws Exception;
}
