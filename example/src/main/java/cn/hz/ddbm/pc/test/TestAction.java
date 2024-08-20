package cn.hz.ddbm.pc.test;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

public class TestAction implements Action {
    @Override
    public String beanName() {
        return "test";
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {

    }


}
