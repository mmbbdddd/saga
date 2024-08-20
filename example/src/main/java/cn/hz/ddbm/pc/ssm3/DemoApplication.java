//package cn.hz.ddbm.pc.factory.buider.ssm3;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.BeanFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.statemachine.StateMachine;
//
//@SpringBootApplication
//@Slf4j
//public class DemoApplication implements CommandLineRunner {
//
//    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication.class, args);
//    }
//
//    @Autowired
//    private SSMConfig orderStateMachineBuilder;
//
//    @Autowired
//    private BeanFactory beanFactory;
//
//    @Override
//    public void run(String... args) throws Exception {
//        log.info("# 工作流开始");
//        StateMachine<SSM3.OrderStates, SSM3.OrderEvents> stateMachine = orderStateMachineBuilder.build(beanFactory);
//
//        stateMachine.start();
//        // 触发 支付，发货，收货 等事件
//        stateMachine.sendEvent(SSM3.OrderEvents.PAY);
//        stateMachine.sendEvent(SSM3.OrderEvents.DELIVER);
//        stateMachine.sendEvent(SSM3.OrderEvents.RECEIVE);
//
//    }
//}