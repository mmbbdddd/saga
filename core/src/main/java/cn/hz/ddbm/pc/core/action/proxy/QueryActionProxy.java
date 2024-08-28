package cn.hz.ddbm.pc.core.action.proxy;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.QueryAction;

public class QueryActionProxy implements QueryAction {
    QueryAction queryAction;

    public QueryActionProxy(QueryAction queryAction) {
        this.queryAction = queryAction;
    }

    @Override
    public String beanName() {
        return queryAction.beanName();
    }

    @Override
    public Enum queryState(FsmContext ctx) throws Exception {
        return queryAction.queryState(ctx);
    }
}
