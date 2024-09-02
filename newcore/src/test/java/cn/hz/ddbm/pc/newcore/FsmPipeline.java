package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import com.google.common.collect.Lists;

import java.util.*;
import java.util.stream.Collectors;

import static cn.hz.ddbm.pc.newcore.FsmPipeline.Pay.init;
import static cn.hz.ddbm.pc.newcore.FsmPipeline.Pay.send;

public class FsmPipeline {
    List<FsmStateMachine>      pipelines;
    Map<Enum, FsmStateMachine> map;

    public FsmPipeline(List<Enum> states) {
        pipelines = new ArrayList<>();
        map       = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            FsmStateMachine fsm = new FsmStateMachine<>(i, states.get(i), this);
            pipelines.add(fsm);
            map.put(states.get(i), fsm);
        }
    }

    void doIt(FsmStateMachine.FsmContext ctx) throws FlowEndException {
        if (null == ctx.getState()) {
            ctx.state    = pipelines.get(0).state;
            ctx.subState = FsmStateMachine.SubState.task;
        }
        map.get(ctx.getState()).onEvent(ctx);
    }

    public static void main(String[] args) {
        FsmPipeline p = new FsmPipeline(Arrays.stream(Pay.values()).collect(Collectors.toList()));
        try {
            p.doIt(new FsmStateMachine.FsmContext());
        } catch (FlowEndException e) {
            throw new RuntimeException(e);
        }
    }

    enum Pay {
        init,
        pay,
        send
    }
}
