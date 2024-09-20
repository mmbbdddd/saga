package cn.hz.ddbm.pc.newcore.fsm;

import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.actions.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.routers.ToRouter;
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
    public void runFsm() {
        System.setProperty(Coast.RUN_MODE, Coast.RUN_MODE_CHAOS);
        FsmFlow<IdCard> p = new FsmFlow<>(IdCard.init, IdCard.su, IdCard.fail);
        p.local(IdCard.init, "push", PrepareAction.class, new ToRouter<>(IdCard.presend));
        p.local(IdCard.presend, "push", PrepareAction.class, new ToRouter<>(IdCard.auditing));
        p.local(IdCard.auditing, "push", PrepareAction.class, new Router<>(new RowKeyTable<String, IdCard, Double>() {{
            put("result.code == '0000'", IdCard.su, 1.0);
            put("result.code == '0001'", IdCard.fail, 0.1);
            put("result.code == '0002'", IdCard.no_such_order, 0.1);
            put("result.code == '0003'", IdCard.lost_date, 0.1);
        }}));
        p.local(IdCard.no_such_order, "push", null, new ToRouter<>(IdCard.presend));
        p.local(IdCard.lost_date, "push", null, new ToRouter<>(IdCard.init));

        FsmContext<IdCard> ctx = new FsmContext<>();
        ctx.flow = p;
        ctx.state = new FsmState<>();
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
        SpringUtil springUtil() {
            return new SpringUtil();
        }
    }

    class PrepareAction implements LocalFsmAction {
        @Override
        public Object localFsm(FsmContext ctx) throws Exception {
            return null;
        }
    }
}