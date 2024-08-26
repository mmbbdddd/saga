package cn.hz.ddbm.pc.core.action.type;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.QueryAction;

public class QueryActionDecorator implements QueryAction {
    QueryAction queryAction;

    public QueryActionDecorator( QueryAction queryAction) {
        this.queryAction = queryAction;
    }

    @Override
    public String beanName() {
        return queryAction.beanName();
    }


    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return queryAction.query(ctx);
    }

}
