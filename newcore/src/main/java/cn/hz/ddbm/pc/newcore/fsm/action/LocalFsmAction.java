package cn.hz.ddbm.pc.newcore.fsm.action;

import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

public interface LocalFsmAction <S extends Enum<S>> extends FsmAction<S> {


    @Override
    default Object executeQuery(FsmContext<S> ctx) throws Exception {
        return null;
    }
}
