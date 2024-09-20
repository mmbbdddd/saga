package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * 本地action
 *
 * @param <S>
 */
public interface LocalFsmAction<S extends Enum<S>> extends FsmAction {

    /**
     * 执行业务逻辑
     *
     * @param ctx
     * @throws Exception
     */
    Object localFsm(FsmContext<S> ctx) throws Exception;

}
