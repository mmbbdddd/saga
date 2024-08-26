package cn.hz.ddbm.pc.core.action.decorator;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

public class QueryActionDecorator implements QueryAction {
    String      beanName;
    QueryAction queryAction;

    public QueryActionDecorator(String beanName, QueryAction queryAction) {
        this.beanName    = beanName;
        this.queryAction = queryAction;
    }

    @Override
    public String beanName() {
        return beanName;
    }


    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return queryAction.query(ctx);
    }

}
