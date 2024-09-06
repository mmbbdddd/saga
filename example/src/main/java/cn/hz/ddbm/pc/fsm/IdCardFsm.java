package cn.hz.ddbm.pc.fsm;

import cn.hz.ddbm.pc.factory.fsm.FSM;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalRouter;
import cn.hz.ddbm.pc.newcore.fsm.router.LocalToRouter;
import com.google.common.collect.Sets;

import java.util.HashMap;
import java.util.List;
import java.util.Set;


public class IdCardFsm implements FSM<IdCardState> {

    @Override
    public String flowId() {
        return "test";
    }

    @Override
    public String describe() {
        return "fsm";
    }

    @Override
    public IdCardState init() {
        return IdCardState.init;
    }

    @Override
    public Set<IdCardState> ends() {
        return Sets.newHashSet(IdCardState.su, IdCardState.fail);
    }

    @Override
    public List<Plugin> plugins() {
        return null;
    }

    @Override
    public Coast.SessionType session() {
        return null;
    }

    @Override
    public Coast.StatusType status() {
        return null;
    }


    @Override
    public void transitions(Transitions<IdCardState> transitions) {
        transitions.state(IdCardState.init)
                .local(Coast.FSM.EVENT_DEFAULT, LocalFsmAction.class, new LocalToRouter<>(IdCardState.presend))
                .endState()
                .state(IdCardState.presend)
                .local(Coast.FSM.EVENT_DEFAULT, LocalFsmAction.class, new LocalToRouter<>(IdCardState.auditing))
                .endState()
                .state(IdCardState.auditing)
                .local(Coast.FSM.EVENT_DEFAULT, LocalFsmAction.class, new LocalRouter<>(new HashMap<String, IdCardState>() {{
                    put("result.code == '0000'", IdCardState.su);
                    put("result.code == '0001'", IdCardState.fail);
                    put("result.code == '0002'", IdCardState.no_such_order);
                    put("result.code == '0003'", IdCardState.lost_date);
                }}))
                .endState()
                .state(IdCardState.no_such_order)
                .local(Coast.FSM.EVENT_DEFAULT,LocalFsmAction.class,new LocalToRouter<>(IdCardState.presend))
                .endState()
                .state(IdCardState.lost_date)
                .local(Coast.FSM.EVENT_DEFAULT,LocalFsmAction.class,new LocalToRouter<>(IdCardState.init))
                .endState()
        ;
    }

    @Override
    public Profile profile() {
        return Profile.devOf();
    }

    @Override
    public Class<IdCardState> type() {
        return IdCardState.class;
    }
}
