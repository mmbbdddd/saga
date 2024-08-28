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
    public Enum queryState(FsmContext ctx) throws Exception {
        return null;
    }

    @Override
    public Boolean executeWhen(FsmContext  ctx) throws Exception {
        //todo
        return null;
    }
}
