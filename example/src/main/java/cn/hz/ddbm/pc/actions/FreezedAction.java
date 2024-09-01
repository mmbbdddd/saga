//package cn.hz.ddbm.pc.actions;
//
//import cn.hz.ddbm.pc.newcore.exception.ActionException;
//import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
//import cn.hz.ddbm.pc.newcore.saga.SagaAction;
//import cn.hz.ddbm.pc.newcore.saga.SagaContext;
//import lombok.Setter;
//import org.springframework.stereotype.Component;
//
//@Component
//public class FreezedAction implements SagaAction {
//    @Setter
//    String code;
//    @Override
//    public String code() {
//        return code;
//    }
//
//    @Override
//    public void execute(SagaContext<?> ctx) throws ActionException {
//
//    }
//
//    @Override
//    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
//        return null;
//    }
//
//    @Override
//    public void rollback(SagaContext<?> ctx) throws ActionException {
//
//    }
//
//    @Override
//    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
//        return null;
//    }
//
//
////    @Override
////    public Boolean executeWhen(FsmContext<PayState, ?> ctx) throws Exception {
////        return Objects.equals(ctx.getState(), PayState.init);
////    }
////
////    @Override
////    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
////        PayTest.account.decrementAndGet();
////        PayTest.freezed.incrementAndGet();
//////        Logs.flow.info("{},{}支付扣款", ctx.getFlow().getName(), ctx.getId());
////        ctx.getSession("");
////    }
////
////
////    @Override
////    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
//////        return RandomUitl.random(Lists.newArrayList(PayState.init, PayState.freezed, freezed_rollback, PayState.freezed_failover));
////        return RandomUitl.selectByWeight("f1", Sets.set(
////                Pair.of(PayState.init,0.1),
////                Pair.of(PayState.freezed,0.7),
////                Pair.of(freezed_rollback,0.1),
////                Pair.of(PayState.freezed_failover,0.1)
////        ));
////    }
//
//}
