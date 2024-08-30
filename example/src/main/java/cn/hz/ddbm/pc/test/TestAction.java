package cn.hz.ddbm.pc.test;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.action.QueryAction;

public class TestAction implements QueryAction {
    @Override
    public String beanName() {
        return "test";
    }

    @Override
    public State queryState(FsmContext ctx) throws Exception {
        return null;
    }


//    @Override
//    public Enum queryState(FsmContext ctx) throws Exception {
//        return null;
//    }
}
