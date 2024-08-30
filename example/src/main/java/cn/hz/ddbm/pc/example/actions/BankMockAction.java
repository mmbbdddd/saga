package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.NoSuchRecordException;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.example.PayState;
import org.springframework.stereotype.Component;

@Component
public class BankMockAction implements SagaAction  {

    public BankMockAction() {
    }

    @Override
    public String beanName() {
        return "bankMockAction";
    }

    @Override
    public void exec(FsmContext ctx) throws Exception {

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


//
//    @Override
//    public Boolean executeWhen(FsmContext<PayState, ?> ctx) {
//        return Objects.equals(ctx.getState(),PayState.payed);
//    }
//    @Override
//    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
//        PayTest.bank.incrementAndGet();
//    }
//
//
//    @Override
//    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
////        return RandomUitl.random(Lists.newArrayList(PayState.payed, PayState.su, PayState.manual, PayState.payed_failover));
//        return RandomUitl.selectByWeight("f4", Sets.set(
//                Pair.of(PayState.payed,0.1),
//                Pair.of(PayState.su,0.7),
//                Pair.of(PayState.manual,0.1),
//                Pair.of(PayState.payed_failover,0.1)
//        ));



}
