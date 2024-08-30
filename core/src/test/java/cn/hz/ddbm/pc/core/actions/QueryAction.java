package cn.hz.ddbm.pc.core.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;
import org.springframework.stereotype.Component;

@Component
public class QueryAction implements cn.hz.ddbm.pc.core.action.QueryAction {

    @Override
    public String beanName() {
        return "queryAction";
    }

    @Override
    public State queryState(FsmContext ctx) throws Exception {
        return null;
    }



}
