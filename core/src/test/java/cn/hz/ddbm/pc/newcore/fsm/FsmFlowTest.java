package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcessorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.chaos.LocalChaosAction;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.routers.ToRouter;
import org.codehaus.groovy.transform.sc.transformers.RangeExpressionTransformer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class FsmFlowTest {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();

    @Before
    public void setup() {
        ctx.register(FF.class);
        ctx.refresh();
    }


    @Test
    public void runFsm() throws Exception {
//        EnvUtils.setRunModeChaos();
        FsmFlow  p = new FsmFlow(IdCard.init, IdCard.su, IdCard.fail);
        p.local(IdCard.init, "push", PrepareAction.class, new ToRouter<>(IdCard.presend));
        p.local(IdCard.presend, "push", PrepareAction.class, new ToRouter<>(IdCard.auditing));
        p.local(IdCard.auditing, "push", PrepareAction.class, new Router(new RowKeyTable<String, IdCard, Double>() {{
            put("result.code == '0000'", IdCard.su, 1.0);
            put("result.code == '0001'", IdCard.fail, 0.1);
            put("result.code == '0002'", IdCard.no_such_order, 0.1);
            put("result.code == '0003'", IdCard.lost_date, 0.1);
        }}));
        p.local(IdCard.no_such_order, "push", PrepareAction.class, new ToRouter<>(IdCard.presend));
        p.local(IdCard.lost_date, "push", PrepareAction.class, new ToRouter<>(IdCard.init));

        p.profile(Profile.chaosOf());

        FlowContext<FsmState> ctx = new FlowContext<FsmState>();
        ctx.flow = p;

        ctx.state = new FsmState();
        ctx.state.setFlowStatus(FlowStatus.RUNNABLE);
        ctx.state.setState(IdCard.init);
        ctx.state.setOffset(FsmWorker.Offset.task);
        ctx.setEvent("push");
        p.execute(ctx);
    }

    enum IdCard {
        init,
        presend,
        auditing,
        no_such_order,
        lost_date,
        su,
        fail,

    }

    static class FF {
        @Bean
        LocalChaosAction localChaosAction() {
            return new LocalChaosAction();
        }

        @Bean
        SpringUtil springUtil() {
            return new SpringUtil();
        }

        @Bean
        PrepareAction prepareAction() {
            return new PrepareAction();
        }

        @Bean
        ProcessorService procesorService() {
            return new ProcessorService();
        }
    }

    static class PrepareAction implements LocalFsmAction {
        @Override
        public Object doLocalFsm(FlowContext<FsmState > ctx) throws Exception {
            Long    executeTimes = SpringUtil.getBean(ProcessorService.class).getExecuteTimes(ctx);
            Integer retryTimes   = ctx.getFlow().getRetry(ctx.state);
            if (executeTimes > retryTimes) {
                throw new RuntimeException("2");
            }
            Double r = Math.random();
            if(r<0.1){
                throw new Exception("1");
            }
            if(r<0.3){
                throw new RuntimeException("1");
            }
            return null;
        }
    }
}