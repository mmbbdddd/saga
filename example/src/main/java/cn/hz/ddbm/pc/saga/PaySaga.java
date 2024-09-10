package cn.hz.ddbm.pc.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.factory.saga.SAGA;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.test.NoneSagaAction;
import cn.hz.ddbm.pc.saga.actions.SagaEndAction;
import cn.hz.ddbm.pc.saga.actions.SagaFreezeAction;
import cn.hz.ddbm.pc.saga.actions.SagaPayAction;
import cn.hz.ddbm.pc.saga.actions.SagaSendAction;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PaySaga implements SAGA<PayState> {

    @Override
    public String flowId() {
        return "test";
    }

    @Override
    public List<Pair<PayState, Class<? extends SagaAction>>> pipeline() {
        return new ArrayList<Pair<PayState, Class<? extends SagaAction>>>() {{
            add(Pair.of(PayState.freeze, SagaFreezeAction.class));
            add(Pair.of(PayState.send, SagaSendAction.class));
            add(Pair.of(PayState.pay, SagaPayAction.class));
        }};

    }

    @Override
    public List<Plugin> plugins() {
        return new ArrayList<Plugin>() {{
//            add(new SagaDigestPlugin());
        }};
    }

    @Override
    public Profile profile() {
        return Profile.chaosOf();
    }
}
