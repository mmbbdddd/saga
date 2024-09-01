//package cn.hz.ddbm.pc.example;
//
//import cn.hz.ddbm.pc.common.lang.Triple;
//import cn.hz.ddbm.pc.core.FsmPayload;
//import cn.hz.ddbm.pc.core.State;
//import cn.hz.ddbm.pc.core.coast.Coasts;
//import cn.hz.ddbm.pc.core.enums.FlowStatus;
//import cn.hz.ddbm.pc.core.processor.saga.SagaState;
//import cn.hz.ddbm.pc.profile.ChaosSagaService;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.Serializable;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
////@UseFsm(PayFsm.class)
//public class PayService implements InitializingBean {
//    static Map<Long, PayOrder> orders;
//    @Autowired
//    ChaosSagaService pcService;
//
//    //每个业务方法前后增加事件拦截。自动发出push事件。
////    @FsmWatch(ids = {"$order.id"})
//    public void saveOrder(PayOrder order) {
//        order.status = FlowStatus.INIT;
//        order.state  = SagaState.of(PayState.init);
//        orders.put(order.orderId, order);
//        try {
//            pcService.execute("pay", order, Coasts.EVENT_FORWARD);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public PayOrder query(Long orderId) {
//        return PayService.orders.get(orderId);
//    }
//
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        PayService.orders = new HashMap<>();
//    }
//
//    public static class PayOrder implements FsmPayload<SagaState<PayState>> {
//        Long                orderId;
//        FlowStatus          status;
//        SagaState<PayState> state;
//        String              event;
//
//
//        @Override
//        public Serializable getId() {
//            return orderId;
//        }
//
//        @Override
//        public Triple<FlowStatus, SagaState<PayState>, String> getStatus() {
//            return Triple.of(status, state, event);
//        }
//
//        @Override
//        public void setStatus(Triple<FlowStatus, SagaState<PayState>, String> status) {
//            this.status = status.getLeft();
//            this.state  = status.getMiddle();
//            this.event  = status.getRight();
//        }
//    }
//}
