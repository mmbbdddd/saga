package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.ProcessorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;

/**
 * @param <S>
 */
public class RemoteFsmActionProxy  {
    Class<? extends RemoteFsmAction> actionClass;
    RemoteFsmAction               action;

    public RemoteFsmActionProxy(Class<? extends RemoteFsmAction> actionClass) {
        this.actionClass = actionClass;
        this.action = ProcessorService.getAction(actionClass);
    }


    public void doRemoteFsm(FlowContext<FsmState > ctx) {

    }

    public Object remoteFsmQuery(FlowContext<FsmState > ctx) {

        return null;
    }
}
