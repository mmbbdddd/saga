//package cn.hz.ddbm.pc.factory.buider.ssm2;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.statemachine.StateMachineContext;
//import org.springframework.statemachine.StateMachinePersist;
//import org.springframework.statemachine.persist.DefaultStateMachinePersister;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class OrderPersist {
//
//
//    /**
//     * 持久化配置
//     * 在实际使用中，可以配合数据库或者Redis等进行持久化操作
//     * @return
//     */
//    @Bean
//    public DefaultStateMachinePersister<OrderStatusEnum, OrderStatusOperateEventEnum, OrderDO> stateMachinePersister(){
//        Map<OrderDO, StateMachineContext<OrderStatusEnum, OrderStatusOperateEventEnum>> map = new HashMap();
//        return new DefaultStateMachinePersister<>(new StateMachinePersist<OrderStatusEnum, OrderStatusOperateEventEnum, OrderDO>() {
//            @Override
//            public void write(StateMachineContext<OrderStatusEnum, OrderStatusOperateEventEnum> context, OrderDO order) throws Exception {
//                //持久化操作
//                map.put(order, context);
//            }
//
//            @Override
//            public StateMachineContext<OrderStatusEnum, OrderStatusOperateEventEnum> read(OrderDO order) throws Exception {
//                //从库中或者redis中读取order的状态信息
//                return map.get(order);
//            }
//        });
//    }
//}