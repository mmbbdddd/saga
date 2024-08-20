package cn.hz.ddbm.pc.test


import cn.hz.ddbm.pc.core.Fsm
import cn.hz.ddbm.pc.core.Node
import org.junit.Assert
import spock.lang.Specification

class FlowTest extends Specification {


    void cleanup() {}

    def "Of"() {
        expect:
        Fsm f = Fsm.devOf(name, "测试流程", [
                new Node(Node.Type.START, "init", null,),
                new Node(Node.Type.TASK, "pay", null,),
                new Node(Node.Type.END, "pay_error", null,)
        ] as Set, [])
        String.format("%s:%s:%s", f.name, f.profile.sessionManager, f.profile.statusManager) == result
        where:
        name | session | status | result
//        null | null|null|null|null
        "i"  | null    | null   | String.format("%s:%s:%s", "i", "memory", "memory")


    }

    def "AddNode"() {}

    def "AddRouter"() {
        when:
        Fsm f = Fsm.devOf("test", "测试流程", [
                new Node(Node.Type.START, "init", null,),
                new Node(Node.Type.TASK, "pay", null,),
                new Node(Node.Type.END, "pay_error", null,)
        ] as Set, [])


        then:
//        System.out.println(JSON.toJSONString(f.getFsmTable().getRecords(), SerializerFeature.PrettyFormat))
        System.out.println(Arrays.toString(f.getEventTable().getRecords()))

    }

    def "OnEventRouter"() {}

    def "OnEventTo"() {}

//    def "Execute"() {
//        expect:
//        FlowPayload date = new PayloadMock("init");
//        String event = Coasts.EVENT_DEFAULT;
//        Fsm flow = Fsm.devOf("test", "测试流程", [
//                new Node(Node.Type.START, "init", null,),
//                new Node(Node.Type.TASK, "pay", null,),
//                new Node(Node.Type.END, "pay_error", null,)
//        ] as Set, [])
//        FlowContext ctx = new FlowContext(flow, date, event, Profile.defaultOf())
//        ctx.getStatus().node == result
//
//        flow.execute(ctx)
//
//        where:
//        flowStatus                | nodeStatus | result
////        null                       | null       | "flowStatus is null"
////        Flow.STAUS.RUNNABLE.name() | null       | null
//        Fsm.STAUS.RUNNABLE.name() | "init"     | "init"
//
//    }

    def "StartStep"() {
        when:
        Fsm flow = Fsm.devOf("test", "测试流程", [
                new Node(Node.Type.START, "init", null,),
                new Node(Node.Type.TASK, "pay", null,),
                new Node(Node.Type.END, "pay_error", null,)
        ] as Set, [])
        def node = flow.startStep()
        then:
        Assert.assertTrue(node.name == "init")
    }

    def "NodeNames"() {
        when:
        Fsm flow = Fsm.devOf("test", "测试流程", [
                new Node(Node.Type.START, "init", null,),
                new Node(Node.Type.TASK, "pay", null,),
                new Node(Node.Type.END, "pay_error", null,)
        ] as Set, [])
        def names = flow.nodeNames()
        then:
        Assert.assertTrue(["init", "pay", "pay_error"] as Set == names)
    }


}
