package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * 本地action
 * @param <S>
 */
public interface LocalFsmAction<S extends Enum<S>> extends FsmAction<S> {


    @Override
    default Object executeQuery(FsmContext<S> ctx) throws Exception {
        return null;
    }
}
