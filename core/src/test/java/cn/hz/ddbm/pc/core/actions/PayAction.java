package cn.hz.ddbm.pc.core.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.SagaAction;
import org.springframework.stereotype.Component;

@Component
public class PayAction implements SagaAction {
    @Override
    public String beanName() {
        return "payAction";
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {
    }


    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return null;
    }
}
