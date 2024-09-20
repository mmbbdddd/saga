//package cn.hz.ddbm.pc.status.dao;
//
//import cn.hz.ddbm.pc.newcore.State;
//import cn.hz.ddbm.pc.newcore.config.Coast;
//import cn.hz.ddbm.pc.newcore.exception.IdempotentException;
//import cn.hz.ddbm.pc.newcore.exception.StatusException;
//import cn.hz.ddbm.pc.newcore.infra.StatusManager;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//
//import java.io.Serializable;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class DaoStatusManager implements StatusManager, InitializingBean, ApplicationContextAware {
//    private Map<String, PayloadDao<?>> flowDaoMap;
//    private ApplicationContext         ctx;
////
////    @Override
////    public Type code() {
////        return Type.dao;
////    }
////
////    @Override
////    public void setStatus(String flow, Serializable flowId, Triple<FlowStatus, ?, String> statusPair, Integer timeout, FsmContext ctx) throws IOException {
////        flowDaoMap.get(flow).save(ctx.getData());
////    }
////
////    @Override
////    public Triple<FlowStatus, ?, String> getStatus(String flow, Serializable flowId) throws IOException {
////        FsmPayload payload = flowDaoMap.get(flow).get(flow);
////        return payload.getStatus();
////    }
//
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        flowDaoMap = ctx.getBeansOfType(PayloadDao.class).values().stream().collect(Collectors.toMap(
//                PayloadDao::flowName,
//                t -> t
//        ));
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.ctx = applicationContext;
//    }
//
//    @Override
//    public Coast.StatusType code() {
//        return null;
//    }
//
//    @Override
//    public void setStatus(String flow, Serializable flowId, State state, Integer timeout) throws StatusException {
//
//    }
//
//    @Override
//    public State getStatus(String flow, Serializable flowId) throws StatusException {
//        return null;
//    }
//
//    @Override
//    public void idempotent(String key) throws IdempotentException {
//
//    }
//
//    @Override
//    public void unidempotent(String key)   {
//
//    }
//}
