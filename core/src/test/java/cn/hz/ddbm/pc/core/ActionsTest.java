package cn.hz.ddbm.pc.core;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.core.action.Action;
import cn.hz.ddbm.pc.core.action.QueryAction;
import cn.hz.ddbm.pc.core.action.SagaAction;
import cn.hz.ddbm.pc.core.action.proxy.QueryActionProxy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;

public class ActionsTest {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

    @Before
    public void setup() {
        ctx.register(CC.class);
        ctx.refresh();

    }

    @Test
    public void testTypeOf() throws Exception {
        Actions.actionDsl(null, SagaAction.class);
    }

    @Test
    public void testActionDsl() throws Exception {

    }

    static class CC {
        @Bean
        SpringUtil springUtil() {
            return new SpringUtil();
        }

        @Bean
        Action a() {
            return new MockAction();
        }

        @Bean
        Action b() {
            return new Mock2Action();
        }

        @Bean
        Action c() {
            return new MockAction();
        }
    }

    static class MockAction implements Action {

        @Override
        public String beanName() {
            return null;
        }
    }

    static class Mock2Action implements QueryAction {

        @Override
        public String beanName() {
            return null;
        }

        @Override
        public Enum query(FsmContext ctx) throws Exception {
            return null;
        }
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme