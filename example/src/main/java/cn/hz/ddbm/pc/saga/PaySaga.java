package cn.hz.ddbm.pc.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.factory.saga.SAGA;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
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
        return new ArrayList<>();

//        new ArrayList<Triple<PayState, PayState, Class<? extends Action>>>() {{
//            add(Triple.of(PayState.init, PayState.fail, SagaFreezeAction.class));
//            add(Triple.of(PayState.freezed, PayState.freeze_rollback, SagaSendAction.class));
//            add(Triple.of(PayState.sended, PayState.send_rollback, SagaPayAction.class));
//            add(Triple.of(PayState.su, PayState.error, null));
//        }}
    }

    @Override
    public List<Plugin> plugins() {
        return new ArrayList<Plugin>() {{
            add(new SagaDigestPlugin());
        }};
    }

    @Override
    public Profile profile() {
        return Profile.chaosOf();
    }
}
