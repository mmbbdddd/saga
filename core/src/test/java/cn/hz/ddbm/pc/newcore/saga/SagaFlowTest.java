package cn.hz.ddbm.pc.newcore.saga;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.saga.actions.LocalSagaAction;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.Assert.*;

public class SagaFlowTest {

    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

    @Before
    public void setup() {
        ctx.register(SagaFlowTest.CC.class);
        ctx.refresh();
    }

    @Test
    public void runSaga() {
        SagaFlow p = SagaFlow.of(SagaFlowTest.FreezedAction.class, SagaFlowTest.PayAction.class, SagaFlowTest.CommitAction.class);

        SagaContext ctx = new SagaContext();
        ctx.flow         = p;
        ctx.state        = new SagaState();
        ctx.state.index  = 0;
        ctx.state.offset = SagaWorker.Offset.task;
        ctx.state.setFlowStatus(FlowStatus.RUNNABLE);
        p.execute(ctx);

    }

    static class CC {
        @Bean
        SpringUtil springUtil() {
            return new SpringUtil();
        }

        @Bean
        SagaFlowTest.FreezedAction freezedAction() {
            return new SagaFlowTest.FreezedAction();
        }

        @Bean
        SagaFlowTest.PayAction payAction() {
            return new SagaFlowTest.PayAction();
        }

        @Bean
        SagaFlowTest.CommitAction commitAction() {
            return new SagaFlowTest.CommitAction();
        }
    }


    public static class FreezedAction implements LocalSagaAction {

        @Override
        public void doSagaRollback(SagaContext ctx) {

        }

        @Override
        public void doSaga(SagaContext ctx) {

        }
    }

    public static class PayAction implements LocalSagaAction {


        @Override
        public void doSagaRollback(SagaContext ctx) {

        }

        @Override
        public void doSaga(SagaContext ctx) {

        }
    }

    public static class CommitAction implements LocalSagaAction {


        @Override
        public void doSagaRollback(SagaContext ctx) {

        }

        @Override
        public void doSaga(SagaContext ctx) {

        }
    }
}