package cn.hz.ddbm.pc.core.action.proxy;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.QueryAction;
import org.springframework.context.annotation.ScopeMetadata;
import sun.security.krb5.internal.LastReqEntry;

public class FsmActionProxy implements QueryAction, CommandAction {
    QueryAction   queryAction;
    CommandAction commandAction;

    public FsmActionProxy(Action action) {
        if (action instanceof QueryAction) {
            this.queryAction = (QueryAction) action;
        } else {
            this.commandAction = (CommandAction) action;
        }
    }

    @Override
    public String beanName() {
        return queryAction.beanName();
    }

    @Override
    public State execute(FsmContext ctx) throws Exception {
        return null;
    }

    @Override
    public State queryState(FsmContext ctx) throws Exception {
        return null;
    }

//    @Override
//    public Enum queryState(FsmContext ctx) throws Exception {
//        return queryAction.queryState(ctx);
//    }
//
//    @Override
//    public Enum execute(FsmContext ctx) throws Exception {
//        return commandAction.execute(ctx);
//    }
}
