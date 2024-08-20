//package cn.hz.ddbm.pc.factory.buider.ssm2;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.statemachine.config.EnableStateMachine;
//import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
//import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
//import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
//
//import java.util.EnumSet;
//
//@Configuration
//@EnableStateMachine(name = "orderStateMachine")
//public class OrderStatusMachineConfig extends StateMachineConfigurerAdapter<OrderStatusEnum, OrderStatusOperateEventEnum> {
//
//    /**
//     * 设置状态机的状态
//     * StateMachineStateConfigurer 即 状态机状态配置
//     * @param states 状态机状态
//     * @throws Exception 异常
//     */
//    @Override
//    public void configure(StateMachineStateConfigurer<OrderStatusEnum, OrderStatusOperateEventEnum> states) throws Exception {
//        states.withStates()
//                .initial(OrderStatusEnum.DRAFT)
//                .end(OrderStatusEnum.FINISHED)
//                .states(EnumSet.allOf(OrderStatusEnum.class));
//    }
//
//    /**
//     * 设置状态机与订单状态操作事件绑定
//     * StateMachineTransitionConfigurer
//     * @param transitions
//     * @throws Exception
//     */
//    @Override
//    public void configure(StateMachineTransitionConfigurer<OrderStatusEnum, OrderStatusOperateEventEnum> transitions) throws Exception {
//        transitions.withExternal().source(OrderStatusEnum.DRAFT).target(OrderStatusEnum.SUBMITTED)
//                .event(OrderStatusOperateEventEnum.CONFIRMED)
//                .and()
//                .withExternal().source(OrderStatusEnum.SUBMITTED).target(OrderStatusEnum.DELIVERING)
//                .event(OrderStatusOperateEventEnum.DELIVERY)
//                .and()
//                .withExternal().source(OrderStatusEnum.DELIVERING).target(OrderStatusEnum.SIGNED)
//                .event(OrderStatusOperateEventEnum.RECEIVED)
//                .and()
//                .withExternal().source(OrderStatusEnum.SIGNED).target(OrderStatusEnum.FINISHED)
//                .event(OrderStatusOperateEventEnum.CONFIRMED_FINISH);
//
//    }
//}
