package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Action;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
import cn.hz.ddbm.pc.newcore.exception.StatusException;

import java.io.Serializable;

public class ToFsmWorker<S extends Serializable> extends FsmWorker<S> implements CommandAction {

    S                  from;
    S                  to;
    String             action;
    CommandActionProxy commandAction;

    public ToFsmWorker(S from, String commandAction, S to) {
        this.from          = from;
        this.action        = commandAction;
        this.to            = to;
        this.commandAction = new CommandActionProxy(this.action);
    }

    @Override
    public void execute(FsmContext<S> ctx) throws StatusException, IdempotentException, ActionException {

    }


    @Override
    public String code() {
        return commandAction.code();
    }


}