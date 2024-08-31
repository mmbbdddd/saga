package cn.hz.ddbm.pc.example.actions;

import cn.hz.ddbm.pc.example.PayState;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class PayAction implements SagaAction {
    @Setter
    String code;
    @Override
    public String code() {
        return code;
    }

    @Override
    public void execute(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }

    @Override
    public void rollback(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }


//    @Override
//    public Boolean executeWhen(FsmContext<PayState, ?> ctx) throws Exception {
//        return Objects.equals(ctx.getState(), PayState.init);
//    }
//
//    @Override
//    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
//        PayTest.freezed.decrementAndGet();
////        Logs.flow.info("{},{}支付扣款", ctx.getFlow().getName(), ctx.getId());
//        ctx.getSession("");
//    }
//
//
//    @Override
//    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
////        return RandomUitl.random(Lists.newArrayList(PayState.sended, PayState.payed,PayState.freezed_rollback, PayState.payed_failover));
//        return RandomUitl.selectByWeight("f3", Sets.set(
//                Pair.of(PayState.sended,0.1),
//                Pair.of(PayState.su,0.7),
//                Pair.of(PayState.freezed_rollback,0.1),
//                Pair.of(PayState.payed_failover,0.1)
//        ));
//
//    }

}
