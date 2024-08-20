package cn.hz.ddbm.pc.ssm3
//package cn.hz.ddbm.pc.factory.buider.ssm3
//
//import lombok.extern.slf4j.Slf4j
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import org.springframework.beans.factory.BeanFactory
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.statemachine.StateContext
//import org.springframework.statemachine.StateMachine
//import org.springframework.statemachine.action.Action
//import org.springframework.statemachine.annotation.OnTransition
//import org.springframework.statemachine.annotation.WithStateMachine
//import org.springframework.statemachine.config.EnableStateMachine
//import org.springframework.statemachine.config.StateMachineBuilder
//import org.springframework.stereotype.Component
//
//import static cn.hz.ddbm.pc.factory.buider.ssm3.SSM3.OrderEvents.*
//import static cn.hz.ddbm.pc.factory.buider.ssm3.SSM3.OrderStates.*
//
//@EnableStateMachine
//@Configuration
//@Slf4j
//public class SSMConfig {
//    private Logger logger = LoggerFactory.getLogger(getClass());
//    public final static String MACHINEID = "orderStateMachine";
//
//    public StateMachine<SSM3.OrderStates, SSM3.OrderEvents> build(BeanFactory beanFactory) throws Exception {
//        StateMachineBuilder.Builder<SSM3.OrderStates, SSM3.OrderEvents> builder = StateMachineBuilder.builder();
//        Logger logger = LoggerFactory.getLogger(getClass());
//        logger.info("构建订单状态机");
//
//        builder.configureConfiguration()
//                .withConfiguration()
//                .machineId(MACHINEID)
//                .beanFactory(beanFactory);
//
//        builder.configureStates()
//                .withStates()
//                .initial(UNPAID)
//                .states(EnumSet.allOf(SSM3.OrderStates.class));
//
//        builder.configureTransitions()
//                .withExternal()
//                .source(UNPAID).target(WAITING_FOR_DELIVER)
//                .event(PAY)
//        // 加了 action 和捕获异常的 action
//                .action(action(), errorAction())
//                .and()
//                .withExternal()
//                .source(WAITING_FOR_DELIVER).target(WAITING_FOR_RECEIVE)
//                .event(DELIVER)
//                .and()
//                .withExternal()
//                .source(WAITING_FOR_RECEIVE).target(DONE)
//                .event(RECEIVE);
//
//        return builder.build();
//    }
//
//    @Bean
//    public Action<SSM3.OrderStates, SSM3.OrderEvents> action() {
//        return new Action<SSM3.OrderStates, SSM3.OrderEvents>() {
//
//            @Override
//            public void execute(StateContext<SSM3.OrderStates, SSM3.OrderEvents> context) {
//                // do something
//                logger.info("action 行为： {}", context.getMessage());
//                // 可以模拟抛出异常，将会触发 errorAction
//                //throw new RuntimeException("xxx 异常");
//
//            }
//        };
//    }
//
//    @Bean
//    public Action<SSM3.OrderStates, SSM3.OrderEvents> errorAction() {
//        return new Action<SSM3.OrderStates, SSM3.OrderEvents>() {
//
//            @Override
//            public void execute(StateContext<SSM3.OrderStates, SSM3.OrderEvents> context) {
//                // RuntimeException("MyError") added to context
//                logger.info("action 行为-捕获异常： {}", context.getMessage());
//                Exception exception = context.getException();
//                exception.getMessage();
//            }
//        };
//    }
//
//
//    @Component
//// 这里指定了 MACHINEID
//    @WithStateMachine(id = MACHINEID)
//    //绑定待监听的状态机
//    public static class OrderEventConfig {
//        private Logger logger = LoggerFactory.getLogger(getClass());
//
//        @OnTransition(target = "UNPAID")
//        public void create() {
//            logger.info("订单创建，待支付!");
//        }
//
//        @OnTransition(source = "UNPAID", target = "WAITING_FOR_DELIVER")
//        public void pay() {
//            logger.info("用户完成支付，待发货!");
//        }
//
//        @OnTransition(source = "WAITING_FOR_DELIVER", target = "WAITING_FOR_RECEIVE")
//        public void deliver() {
//            logger.info("订单已发货，待收货!");
//        }
//
//        @OnTransition(source = "WAITING_FOR_RECEIVE", target = "DONE")
//        public void receive() {
//            logger.info("用户已收货，订单完成!");
//        }
//    }
//}
