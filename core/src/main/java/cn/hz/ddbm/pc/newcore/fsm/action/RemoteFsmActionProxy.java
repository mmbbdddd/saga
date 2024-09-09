package cn.hz.ddbm.pc.newcore.fsm.action;


import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;
import cn.hz.ddbm.pc.newcore.fsm.FsmWorker;

/**
 * @param <S>
 */
public class RemoteFsmActionProxy<S extends Enum<S>> implements RemoteFsmAction<S> {
    Class<? extends RemoteFsmAction> actionClass;
    RemoteFsmAction<S>               action;

    public RemoteFsmActionProxy(Class<? extends RemoteFsmAction> actionClass) {
        this.actionClass = actionClass;
    }


    @Override
    public String code() {
        return getOrInitAction().code();
    }


    private RemoteFsmAction<S> getOrInitAction() {
        if (null == action) {
            synchronized (this) {
                this.action = ProcesorService.getAction(actionClass);
            }
        }
        return action;
    }

    @Override
    public void remoteFsm(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws ActionException {
        try {
            getOrInitAction().remoteFsm(ctx);
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }

    @Override
    public Object remoteFsmQuery(FlowContext<FsmFlow<S>, FsmState<S>, FsmWorker<S>> ctx) throws ActionException {
        try {
            Object result = getOrInitAction().remoteFsmQuery(ctx);
            return result;
        } catch (Exception e) {
            throw new ActionException(e);
        }
    }


}
