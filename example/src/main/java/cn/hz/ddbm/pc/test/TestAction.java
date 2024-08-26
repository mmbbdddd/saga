package cn.hz.ddbm.pc.test;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.QueryAction;

public class TestAction implements QueryAction {
    @Override
    public String beanName() {
        return "test";
    }


    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return null;
    }
}
