//package cn.hz.ddbm.pc.chaos.support;
//
//import cn.hutool.core.lang.Pair;
//import cn.hz.ddbm.pc.newcore.config.Coast;
//import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
//import cn.hz.ddbm.pc.newcore.fsm.action.RemoteFsmAction;
//import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
//import cn.hz.ddbm.pc.newcore.saga.action.LocalSagaAction;
//import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
//import cn.hz.ddbm.pc.newcore.saga.SagaContext;
//import cn.hz.ddbm.pc.newcore.utils.RandomUitl;
//
//import javax.annotation.Resource;
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * 业务逻辑混沌发生类。
// */
//public class ChaosAction implements RemoteSagaAction, RemoteFsmAction, LocalFsmAction, LocalSagaAction {
//    @Resource
//    ChaosHandlerImpl chaosHandler;
//
//    @Override
//    public String code() {
//        return Coast.CHAOS_ACTION;
//    }
//
//    @Override
//    public Object execute(FsmContext ctx) throws Exception {
//        chaosHandler.handle();
//    }
//
//    @Override
//    public Object executeQuery(FsmContext ctx) throws Exception {
//        Thread.sleep(100);
//        chaosHandler.handle();
//        return new Object();
//    }
//
//
//    @Override
//    public void execute(SagaContext<?> ctx) throws Exception {
//        chaosHandler.handle();
//    }
//
//    @Override
//    public Boolean executeQuery(SagaContext<?> ctx) throws Exception {
//        chaosHandler.handle();
//        Set<Pair<Boolean, Double>> fsmQueryResult = new HashSet<>();
//        fsmQueryResult.add(Pair.of(true, 0.8));
//        fsmQueryResult.add(Pair.of(true, 0.2));
//        return RandomUitl.selectByWeight("SagaAction", fsmQueryResult);
//    }
//
//    @Override
//    public void rollback(SagaContext<?> ctx) throws Exception {
//        chaosHandler.handle();
//    }
//
//    @Override
//    public Boolean rollbackQuery(SagaContext<?> ctx) throws Exception {
//        chaosHandler.handle();
//        Set<Pair<Boolean, Double>> fsmQueryResult = new HashSet<>();
//        fsmQueryResult.add(Pair.of(true, 0.8));
//        fsmQueryResult.add(Pair.of(true, 0.2));
//        return RandomUitl.selectByWeight("SagaAction", fsmQueryResult);
//    }
//}
