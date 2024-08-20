package cn.hz.ddbm.pc.test

import cn.hz.ddbm.pc.configuration.PcChaosConfiguration
import cn.hz.ddbm.pc.core.FsmPayload
import cn.hz.ddbm.pc.core.Fsm
import cn.hz.ddbm.pc.core.Node
import cn.hz.ddbm.pc.core.coast.Coasts
import cn.hz.ddbm.pc.profile.ChaosPcService
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Specification

public class ChaosServiceTest extends Specification {

    ChaosPcService chaosService = new ChaosPcService();
    Fsm flow

    public void setup() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()
        ctx.register(PcChaosConfiguration.class)
        ctx.refresh()

        flow = Fsm.devOf("test", "测试流程", [
                new Node(Node.Type.START, "init", null,),
                new Node(Node.Type.TASK, "pay", null,),
                new Node(Node.Type.END, "pay_error", null,)
        ] as Set, [])

        flow.to("init", Coasts.EVENT_DEFAULT, Coasts.NONE, "pay")
        flow.to("pay", Coasts.EVENT_DEFAULT, Coasts.NONE, "pay_error")
        flow.to("pay_error", Coasts.EVENT_DEFAULT, Coasts.NONE, "pay_error")


        chaosService.addFlow(flow)
    }


    def "Execute"() {
        expect:

        FsmPayload date = new ChaosPcService.MockPayLoad("init");
        String event = Coasts.EVENT_DEFAULT;
        chaosService.execute("test", date, event, 100, 10, rules, false)

        where:
        flowStatus                | nodeStatus | result
//        null                       | null       | "flowStatus is null"
//        Flow.STAUS.RUNNABLE.name() | null       | null
        Fsm.STAUS.RUNNABLE.name() | "init"     | "init"
    }


}