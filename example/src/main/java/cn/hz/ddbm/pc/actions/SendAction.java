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
//public class SendAction implements SagaAction {
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
//        return true;
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
////    public Boolean executeWhen(FsmContext<PayState, ?> ctx) {
////        return Objects.equals(ctx.getState(),PayState.payed);
////    }
////    @Override
////    public void execute(FsmContext<PayState, ?> ctx) throws Exception {
////        PayTest.bank.incrementAndGet();
////    }
////
////    @Override
////    public PayState query(FsmContext<PayState, ?> ctx) {
////        return null;
////    }
////
////
////    @Override
////    public PayState queryState(FsmContext<PayState, ?> ctx) throws Exception {
//////        return RandomUitl.random(Lists.newArrayList(PayState.freezed, PayState.sended, PayState.sended_failover));
////        return RandomUitl.selectByWeight("f5", Sets.set(
////                Pair.of(PayState.freezed,0.1),
////                Pair.of(PayState.sended,0.8),
////                Pair.of(PayState.sended_failover,0.1)
////        ));
////    }
//
//
//
//}
