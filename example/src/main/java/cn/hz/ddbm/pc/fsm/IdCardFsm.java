package cn.hz.ddbm.pc.fsm;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.factory.saga.SAGA;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.saga.action.RemoteSagaAction;

import java.util.List;


public class IdCardFsm implements SAGA<IdCardState> {

    @Override
    public String flowId() {
        return null;
    }

    @Override
    public List<Pair<IdCardState, Class<? extends RemoteSagaAction>>> pipeline() {
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
