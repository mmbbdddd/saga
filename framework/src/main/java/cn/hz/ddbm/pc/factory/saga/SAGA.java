package cn.hz.ddbm.pc.factory.saga;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;

import java.util.List;

public interface SAGA<S extends Enum<S>> {
    default SagaFlow<S> build() {
        SagaFlow flow    = new SagaFlow(flowId(), pipeline());
        Profile  profile = profile();
        profile.setPlugins(plugins());
        flow.profile(profile);
        return flow;
    }

    String flowId();

    List<Pair<S, Class<? extends SagaAction>>> pipeline();

    List<Plugin> plugins();

    Profile profile();

}
