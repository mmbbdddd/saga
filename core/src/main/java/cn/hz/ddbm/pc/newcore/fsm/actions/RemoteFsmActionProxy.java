package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * @param <S>
 */
public class RemoteFsmActionProxy<S extends Enum<S>> {
    Class<? extends RemoteFsmAction> actionClass;
    RemoteFsmAction<S>               action;

    public RemoteFsmActionProxy(Class<? extends RemoteFsmAction> actionClass) {
        this.actionClass = actionClass;
        this.action = ProcesorService.getAction(actionClass);
    }


    public void doRemoteFsm(FsmContext<S> ctx) {

    }

    public Object remoteFsmQuery(FsmContext<S> ctx) {

        return null;
    }
}
