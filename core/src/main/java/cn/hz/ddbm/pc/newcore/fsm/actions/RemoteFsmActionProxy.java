package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * @param <S>
 */
public class RemoteFsmActionProxy<S extends Enum<S>> {
    Class<? extends RemoteFsmAction> actionClass;
    RemoteFsmAction<S>               action;

    public RemoteFsmActionProxy(Class<? extends RemoteFsmAction> actionClass) {
        this.actionClass = actionClass;
    }


    public void doRemote(FsmContext<S> ctx) {

    }

    public Object remoteQuery(FsmContext<S> ctx) {

        return null;
    }
}
