//package cn.hz.ddbm.pc.fsm;
//
//import cn.hutool.core.map.multi.RowKeyTable;
//import cn.hz.ddbm.pc.factory.fsm.FSM;
//import cn.hz.ddbm.pc.newcore.Plugin;
//import cn.hz.ddbm.pc.newcore.Profile;
//import cn.hz.ddbm.pc.newcore.config.Coast;
//import cn.hz.ddbm.pc.newcore.fsm.Router;
//import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;
//import cn.hz.ddbm.pc.newcore.fsm.router.ToRouter;
//import cn.hz.ddbm.pc.newcore.plugins.FsmDigestPlugin;
//import com.google.common.collect.Sets;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Set;
//
//
//public class IdCardFsm implements FSM<IdCardState> {
//
//    @Override
//    public String flowId() {
//        return "test";
//    }
//
//    @Override
//    public String describe() {
//        return "fsm";
//    }
//
//    @Override
//    public IdCardState init() {
//        return IdCardState.init;
//    }
//
//    @Override
//    public Set<IdCardState> ends() {
//        return Sets.newHashSet(IdCardState.su, IdCardState.fail);
//    }
//
//    @Override
//    public List<Plugin> plugins() {
//        return new ArrayList<Plugin>() {{
//            add(new FsmDigestPlugin());
//        }};
//    }
//
//    @Override
//    public Coast.SessionType session() {
//        return null;
//    }
//
//    @Override
//    public Coast.StatusType status() {
//        return null;
//    }
//
//
//    @Override
//    public void transitions(Transitions<IdCardState> transitions) {
//        transitions.state(IdCardState.init)
//                .local(Coast.EVENT_DEFAULT, LocalFsmAction.class, new ToRouter<>(IdCardState.presend))
//                .endState()
//                .state(IdCardState.presend)
//                .local(Coast.EVENT_DEFAULT, LocalFsmAction.class, new ToRouter<>(IdCardState.auditing))
//                .endState()
//                .state(IdCardState.auditing)
//                .local(Coast.EVENT_DEFAULT, LocalFsmAction.class, new Router<>(new RowKeyTable<String, IdCardState, Double>() {{
//                    put("result.code == '0000'", IdCardState.su, 1.0);
//                    put("result.code == '0001'", IdCardState.fail, 0.1);
//                    put("result.code == '0002'", IdCardState.no_such_order, 0.1);
//                    put("result.code == '0003'", IdCardState.lost_date, 0.1);
//                }}))
//                .endState()
//                .state(IdCardState.no_such_order)
//                .local(Coast.EVENT_DEFAULT, LocalFsmAction.class, new ToRouter<>(IdCardState.presend))
//                .endState()
//                .state(IdCardState.lost_date)
//                .local(Coast.EVENT_DEFAULT, LocalFsmAction.class, new ToRouter<>(IdCardState.init))
//                .endState()
//        ;
//    }
//
//    @Override
//    public Profile profile() {
//        return Profile.chaosOf();
//    }
//
//    @Override
//    public Class<IdCardState> type() {
//        return IdCardState.class;
//    }
//}
