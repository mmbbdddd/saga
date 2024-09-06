package cn.hz.ddbm.pc.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.factory.saga.SAGA;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;
import cn.hz.ddbm.pc.saga.actions.SagaFreezeAction;
import cn.hz.ddbm.pc.saga.actions.SagaPayAction;
import cn.hz.ddbm.pc.saga.actions.SagaSendAction;

import java.util.ArrayList;
import java.util.List;


public class PaySaga implements SAGA<PayState> {

    @Override
    public String flowId() {
        return "sagaTest";
    }

    @Override
    public List<Pair<PayState, Class<? extends RemoteSagaAction>>> pipeline() {
        return new ArrayList<Pair<PayState, Class<? extends RemoteSagaAction>>>() {{
            add(Pair.of(PayState.init, SagaFreezeAction.class));
            add(Pair.of(PayState.freezed, SagaSendAction.class));
            add(Pair.of(PayState.sended, SagaPayAction.class));
        }};
    }

    @Override
    public List<Plugin> plugins() {
        return new ArrayList<>();
    }

    @Override
    public Profile profile() {
        return Profile.of();
    }
}
