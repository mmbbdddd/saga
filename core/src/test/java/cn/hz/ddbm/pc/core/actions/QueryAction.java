package cn.hz.ddbm.pc.core.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import org.springframework.stereotype.Component;

@Component
public class QueryAction implements cn.hz.ddbm.pc.core.action.QueryAction {

    @Override
    public String beanName() {
        return "queryAction";
    }


    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return null;
    }
}
