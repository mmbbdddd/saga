package cn.hz.ddbm.pc.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.factory.saga.SAGA;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;

import java.util.List;


public class PaySaga implements SAGA<PayState> {

    @Override
    public String flowId() {
        return "sagaTest";
    }

    @Override
    public List<Pair<PayState, Class<? extends SagaAction>>> pipeline() {
        return null;
    }

    @Override
    public List<Plugin> plugins() {
        return null;
    }

    @Override
    public Profile profile() {
        return null;
    }
}
