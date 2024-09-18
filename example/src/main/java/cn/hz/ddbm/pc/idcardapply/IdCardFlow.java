package cn.hz.ddbm.pc.idcardapply;

import cn.hz.ddbm.pc.factory.fsm.FSM;
import cn.hz.ddbm.pc.idcardapply.actions.MaterialCollectionAction;
import cn.hz.ddbm.pc.idcardapply.actions.RuleCheckedAction;
import cn.hz.ddbm.pc.idcardapply.actions.SendBizAction;
import cn.hz.ddbm.pc.newcore.Plugin;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.fsm.Router;
import cn.hz.ddbm.pc.newcore.fsm.router.ToRouter;
import com.google.common.collect.Sets;

import java.util.*;


public class IdCardFlow implements FSM<IdCardFSM> {

    @Override
    public String flowId() {
        return "idcard";
    }

    @Override
    public String describe() {
        return "身份证办理";
    }

    @Override
    public IdCardFSM init() {
        return IdCardFSM.MaterialCollection;
    }

    @Override
    public Set<IdCardFSM> ends() {
        return Sets.newHashSet(IdCardFSM.Pass, IdCardFSM.Reject);
    }

    @Override
    public List<Plugin> plugins() {
        return Collections.emptyList();
    }

    @Override
    public Coast.SessionType session() {
        return Coast.SessionType.jvm;
    }

    @Override
    public Coast.StatusType status() {
        return Coast.StatusType.jvm;
    }

    @Override
    public void transitions(Transitions<IdCardFSM> transitions) {
        transitions
                .state(IdCardFSM.MaterialCollection)
                .local(Coast.EVENT_DEFAULT, MaterialCollectionAction.class, new ToRouter<>(IdCardFSM.RuleChecked))
                .endState()
                .state(IdCardFSM.RuleChecked)
                .local(Coast.EVENT_DEFAULT, RuleCheckedAction.class, new Router<>(new HashMap<String, IdCardFSM>() {{
                    put("true", IdCardFSM.Accepted);
                    put("false", IdCardFSM.MaterialCollection);
                }}))
                .endState()
                .state(IdCardFSM.Accepted)
                .remote(Coast.EVENT_DEFAULT, SendBizAction.class, new Router<>(
                        new HashMap<String, IdCardFSM>() {{
                            put("true", IdCardFSM.RuleSyncing);
                            put("false", IdCardFSM.Accepted);
                            put("false", IdCardFSM.MaterialCollection);
                        }}))
                .endState()
                .state(IdCardFSM.RuleSyncing)
                .remote(Coast.EVENT_DEFAULT, SendBizAction.class, new Router<>(
                        new HashMap<String, IdCardFSM>() {{
                            put("true", IdCardFSM.RuleChecked);
                            put("false", IdCardFSM.RuleSyncing);
                        }}))
                .endState()
        ;
    }

    @Override
    public Profile profile() {
        return Profile.chaosOf();
    }

    @Override
    public Class<IdCardFSM> type() {
        return IdCardFSM.class;
    }
}
