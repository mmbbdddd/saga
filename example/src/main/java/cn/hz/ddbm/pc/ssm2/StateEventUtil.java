//package cn.hz.ddbm.pc.factory.buider.ssm2;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.Message;
//import org.springframework.statemachine.StateMachine;
//import org.springframework.statemachine.persist.StateMachinePersister;
//import org.springframework.stereotype.Component;
//
//import java.util.Objects;
//
//@Component
//public class StateEventUtil {
//
//    private StateMachine<OrderStatusEnum, OrderStatusOperateEventEnum> orderStateMachine;
//
//    private StateMachinePersister<OrderStatusEnum, OrderStatusOperateEventEnum, OrderDO> stateMachinePersister;
//
//    /**
//     * 发送状态转换事件
//     *  synchronized修饰保证这个方法是线程安全的
//     * @param message
//     * @return
//     */
//    public synchronized boolean sendEvent(Message<OrderStatusOperateEventEnum> message) {
//        boolean result = false;
//        try {
//            //启动状态机
//            orderStateMachine.start();
//            OrderDO order = (OrderDO) message.getHeaders().get("order");
//            //尝试恢复状态机状态
//            stateMachinePersister.restore(orderStateMachine, order);
//            result = orderStateMachine.sendEvent(message);
//            //持久化状态机状态
//            stateMachinePersister.persist(orderStateMachine, order);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            if (Objects.nonNull(message)) {
//                OrderDO order = (OrderDO) message.getHeaders().get("order");
//                if (Objects.nonNull(order) && Objects.equals(order.getOrderStatusEnum(), OrderStatusEnum.FINISHED)) {
//                    orderStateMachine.stop();
//                }
//            }
//        }
//        return result;
//    }
//
//    @Autowired
//    public void setOrderStateMachine(StateMachine<OrderStatusEnum, OrderStatusOperateEventEnum> orderStateMachine) {
//        this.orderStateMachine = orderStateMachine;
//    }
//
//    @Autowired
//    public void setStateMachinePersister(StateMachinePersister<OrderStatusEnum, OrderStatusOperateEventEnum, OrderDO> stateMachinePersister) {
//        this.stateMachinePersister = stateMachinePersister;
//    }
//}