//package cn.hz.ddbm.pc.factory.buider.ssm2;
//
//import cn.hz.ddbm.pc.factory.buider.ssm2.OrderDO;
//import cn.hz.ddbm.pc.factory.buider.ssm2.OrderServiceImpl;
//import cn.hz.ddbm.pc.factory.buider.ssm2.OrderStatusEnum;
//import cn.hz.ddbm.pc.factory.buider.ssm2.OrderStatusOperateEventEnum;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.statemachine.StateMachine;
//
//@SpringBootApplication
//public class DemoApplication2 implements CommandLineRunner {
//    public DemoApplication2(StateMachine<OrderStatusEnum, OrderStatusOperateEventEnum> stateMachine) {
//        this.stateMachine = stateMachine;
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(DemoApplication2.class, args);
//        try {
//            Thread.sleep(100000);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//    private final StateMachine<OrderStatusEnum, OrderStatusOperateEventEnum> stateMachine;
//
//    @Autowired
//    private OrderServiceImpl orderService;
//    @Override
//    public void run(String... args) throws Exception {
//        Long orderId1 = orderService.createOrder(new OrderDO());
//        Long orderId2 = orderService.createOrder(new OrderDO());
//
//        orderService.confirmOrder(orderId1);
//        new Thread("客户线程"){
//            @Override
//            public void run() {
//                orderService.deliver(orderId1);
//                orderService.signOrder(orderId1);
//                orderService.finishOrder(orderId1);
//            }
//        }.start();
//
//        orderService.confirmOrder(orderId2);
//        orderService.deliver(orderId2);
//        orderService.signOrder(orderId2);
//        orderService.finishOrder(orderId2);
//
//        System.out.println("全部订单状态：" + orderService.listOrders());
//    }
//}