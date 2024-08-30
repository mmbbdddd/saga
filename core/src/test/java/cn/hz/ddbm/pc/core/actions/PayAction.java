package cn.hz.ddbm.pc.core.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.NoSuchRecordException;
import cn.hz.ddbm.pc.core.action.SagaAction;
import org.springframework.stereotype.Component;

@Component
public class PayAction implements SagaAction {
    @Override
    public String beanName() {
        return "payAction";
    }



    @Override
    public Boolean executeQuery(FsmContext ctx) throws NoSuchRecordException {
        return null;
    }

    @Override
    public void rollback(FsmContext ctx) {

    }

    @Override
    public Boolean rollbackQuery(FsmContext ctx) throws NoSuchRecordException {
        return null;
    }

    @Override
    public void exec(FsmContext ctx) throws Exception {

    }
}
