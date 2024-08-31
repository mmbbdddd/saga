//package cn.hz.ddbm.pc.test
//
//import cn.hz.ddbm.pc.configuration.PcChaosConfiguration
//import cn.hz.ddbm.pc.core.Fsm
//import cn.hz.ddbm.pc.core.enums.FlowStatus
//import cn.hz.ddbm.pc.example.PayState
//import cn.hz.ddbm.pc.profile.ChaosSagaService
//import org.springframework.context.annotation.AnnotationConfigApplicationContext
//import spock.lang.Specification
//
//public class ChaosServiceTest extends Specification {
//
//    ChaosSagaService chaosService = new ChaosSagaService();
//    Fsm                       flow
//    Map<PayState, FlowStatus> map = new HashMap<>();
//
//    public void setup() {
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()
//        ctx.register(PcChaosConfiguration.class)
//        ctx.refresh()
//
//        map.put(PayState.init, FlowStatus.INIT);
//        map.put(PayState.payed, FlowStatus.RUNNABLE);
//        map.put(PayState.sended, FlowStatus.RUNNABLE);
//        map.put(PayState.payed_failover, FlowStatus.RUNNABLE);
//        map.put(PayState.sended_failover, FlowStatus.RUNNABLE);
//        map.put(PayState.su, FlowStatus.FINISH);
//        map.put(PayState.fail, FlowStatus.FINISH);
//        map.put(PayState.error, FlowStatus.FINISH);
//
//
//        flow = Fsm.
//                devOf("test", "测试流程", PayState.init,
//                        [PayState.payed, PayState.sended, PayState.payed_failover, PayState.sended_failover] as Set,
//                        [PayState.su, PayState.fail, PayState.error] as Set)
//
//
//        chaosService.addFlow(flow)
//    }
//
//
//}