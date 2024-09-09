package cn.hz.ddbm.pc.newcore.demo;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Set;


public class Fsm<S extends Enum<S>> {
    Table<S, String, Action<S>> fsmTables = new RowKeyTable<>();
    Set<S>                      ends;

    public Fsm(S... ends) {
        this.fsmTables = new RowKeyTable<>();
        this.ends      = Sets.newHashSet(ends);
    }

    public void execute(String event, FsmConext<S> ctx, Payload<S> pp) {
        S         state = ctx.getState();
        Action<S> ar    = fsmTables.get(state, event);
        if (null == ar) {
            if (isEnd(state)) {
                //updateState();
                System.out.println("保存状态:"+ state.name());
                throw new RuntimeException("flow is end");
            }
            //updateState();
            System.out.println("保存状态:"+ state.name());
            throw new RuntimeException("not find state for event" + event);
        }
        S next = ar.executeTo(ctx);
        ctx.setState(next);
        if (isRunable(ctx)) {
            execute(event, ctx, pp);
        }
    }

    private boolean isEnd(S state) {
        return ends.contains(state);
    }

    interface Payload<S extends Enum<S>> {
    }

    private boolean isRunable(FsmConext<S> ctx) {
        return true;
    }

    public static void main(String[] args) {
        Fsm<IdCardFSM> fsm = new Fsm<>(IdCardFSM.Reject, IdCardFSM.Pass);
        fsm.fsmTables = new RowKeyTable<>();
        fsm.fsmTables.put(IdCardFSM.MaterialCollection, "push", new MockToAction<>(IdCardFSM.RuleChecked));
        fsm.fsmTables.put(IdCardFSM.RuleChecked, "push", new MockRouterAction<>(
                Pair.of("result == true", IdCardFSM.Accepted),
                Pair.of("result == false", IdCardFSM.MaterialCollection)
        ));
        fsm.fsmTables.put(IdCardFSM.Accepted, "push", new MockRouterAction<>(
                Pair.of("result == none", IdCardFSM.Accepted),
                Pair.of("result == ping", IdCardFSM.QueryProcessStatus),
                Pair.of("result == pass", IdCardFSM.Pass),
                Pair.of("result == reject", IdCardFSM.Reject),
                Pair.of("result == missinfo", IdCardFSM.RuleSyncing)
        ));
        fsm.fsmTables.put(IdCardFSM.QueryProcessStatus, "push", new MockRouterAction<>(
                Pair.of("result == none", IdCardFSM.Accepted),
                Pair.of("result == ping", IdCardFSM.QueryProcessStatus),
                Pair.of("result == pass", IdCardFSM.Pass),
                Pair.of("result == reject", IdCardFSM.Reject),
                Pair.of("result == missinfo", IdCardFSM.RuleSyncing)
        ));
        fsm.fsmTables.put(IdCardFSM.RuleSyncing, "push", new MockRouterAction<>(
                Pair.of("result == su", IdCardFSM.RuleChecked),
                Pair.of("result == fail", IdCardFSM.RuleSyncing)
        ));


        fsm.execute("push", new FsmConext(IdCardFSM.MaterialCollection), new Payload() {

        });
    }

    enum IdCardFSM {
        MaterialCollection("收集材料中"),
        RuleChecked("规则检查"),
        Accepted("已受理"),
        QueryProcessStatus("查询处理状态"),
        Pass("通过"),
        Reject("不通过"),
        RuleSyncing("规则同步"),
        ;

        String descr;

        IdCardFSM(String descr) {
            this.descr = descr;
        }
    }

}
