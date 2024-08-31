//package cn.hz.ddbm.pc.test
//
//import cn.hz.ddbm.pc.configuration.PcChaosConfiguration
//import cn.hz.ddbm.pc.example.PayState
//import org.junit.Assert
//import org.springframework.context.annotation.AnnotationConfigApplicationContext
//import spock.lang.Specification
//
//import java.util.stream.Collectors
//
//class FlowTest extends Specification {
//
//    Map<PayState, FlowStatus> map = new HashMap<>();
//
//    void setup() {
//        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
//        ctx.register(PcChaosConfiguration.class)
//        ctx.refresh()
//
//
//        map.put(PayState.init, FlowStatus.INIT);
//        map.put(PayState.payed, FlowStatus.RUNNABLE);
//        map.put(PayState.sended, FlowStatus.RUNNABLE);
//        map.put(PayState.payed_failover, FlowStatus.RUNNABLE);
//        map.put(PayState.sended_failover, FlowStatus.RUNNABLE);
//        map.put(PayState.su, FlowStatus.FINISH);
//        map.put(PayState.fail, FlowStatus.FINISH);
//        map.put(PayState.error, FlowStatus.FINISH);
//    }
//
//    def "Of"() {
//        expect:
//
//
//        Fsm f = Fsm.devOf(name, "测试流程", map)
//        String.format("%s:%s:%s", f.name, f.profile.sessionManager, f.profile.statusManager) == result
//        where:
//        name | session | status | result
////        null | null|null|null|null
//        "i"  | null    | null   | String.format("%s:%s:%s", "i", "memory", "memory")
//
//
//    }
//
//    def "AddNode"() {}
//
//    def "AddRouter"() {
//        when:
//        Fsm f = Fsm.devOf("test", "测试流程", map)
//
//
//        then:
////        System.out.println(JSON.toJSONString(f.getFsmTable().getRecords(), SerializerFeature.PrettyFormat))
//        System.out.println(Arrays.toString(f.getEventTable().getEventTables()))
//
//    }
//
//    def "OnEventRouter"() {}
//
//    def "OnEventTo"() {}
//
////    def "Execute"() {
////        expect:
////        FlowPayload date = new PayloadMock("init");
////        String event = Coasts.EVENT_DEFAULT;
////        Fsm flow = Fsm.devOf("test", "测试流程", [
////                new Node(Node.Type.START, "init", null,),
////                new Node(Node.Type.TASK, "pay", null,),
////                new Node(Node.Type.END, "pay_error", null,)
////        ] as Set, [])
////        FlowContext ctx = new FlowContext(flow, date, event, Profile.defaultOf())
////        ctx.getStatus().node == result
////
////        flow.execute(ctx)
////
////        where:
////        flowStatus                | nodeStatus | result
//////        null                       | null       | "flowStatus is null"
//////        Flow.STAUS.RUNNABLE.name() | null       | null
////        Fsm.STAUS.RUNNABLE.name() | "init"     | "init"
////
////    }
//
//    def "StartStep"() {
//        when:
//        Fsm flow = Fsm.devOf("test", "测试流程", map)
//        def node = flow.startStep()
//        then:
//        Assert.assertTrue(node == PayState.init)
//    }
//
//    def "NodeNames"() {
//        when:
//        Fsm flow = Fsm.devOf("test", "测试流程", map)
//        def names = flow.nodeNames()
//        then:
//        Assert.assertTrue(EnumSet.allOf(PayState).stream().map { t -> t.name() }.collect(Collectors.toSet()) == names)
//    }
//
//
//}
