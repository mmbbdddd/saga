package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.support.ActionResult;
import cn.hz.ddbm.pc.newcore.test.NoneSagaAction;


public class CommandActionProxy implements CommandAction {
    String     action;
    SagaAction sagaAction;

    public CommandActionProxy(String action) {
        this.action = action;
    }

    public CommandActionProxy getOrInitAction() {
        if (null == sagaAction) {
            synchronized (this) {
//                this.sagaAction = getBean(action, SagaAction.class);
                this.sagaAction = new NoneSagaAction();
            }
        }
        return this;
    }



    @Override
    public String code() {
        return action;
    }
}
