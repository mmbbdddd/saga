package cn.hz.ddbm.pc.core

import cn.hutool.extra.spring.SpringUtil
import cn.hz.ddbm.pc.core.action.Action
import cn.hz.ddbm.pc.core.utils.InfraUtils
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import spock.lang.Specification


class ActionTest extends Specification {

    def setup() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext()
        ctx.register(CC.class)
        ctx.refresh()
    }
//
    def "test of"() {
        expect:
        Actions.typeOf(new Fsm.Transition(type, null, null, actionDsl, null, null, null), Action.class, false).beanName() == result
        where:
        type                    | actionDsl               | result
        Fsm.TransitionType.SAGA | "payAction"             | "payAction"
        Fsm.TransitionType.SAGA | "payAction,queryAction" | "payAction,queryAction"
        Fsm.TransitionType.SAGA | "ab,_b"                 | "ab,_b"
    }

    def "exp"() {
        expect:
        str.matches(regex) == result
        where:
        regex           | str      | result
        "\\w+"          | "action" | true
        "(\\w+,)+\\w++" | "a,b"    | true
        "(\\w+,)+\\w++" | "a"      | false
        "(\\w+,)_\\w+"  | "a,_a"   | true

    }

    @ComponentScan("cn.hz.ddbm.pc.core.example.actions")
    static class CC {
        @Bean
        SpringUtil springUtil() {
            return new SpringUtil()
        }

        @Bean
        InfraUtils infraUtils() {
            return new InfraUtils()
        }
    }

}

