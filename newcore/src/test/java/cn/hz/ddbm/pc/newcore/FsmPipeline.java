package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.FlowEndException;

import java.util.*;
import java.util.stream.Collectors;

public class FsmPipeline {
    List<FFF>      pipelines;
    Map<Enum, FFF> map;

    public FsmPipeline(List<Enum> states) {
        pipelines = new ArrayList<>();
        map       = new HashMap<>();
        for (int i = 0; i < states.size(); i++) {
            FFF fsm = new FFF<>(i, states.get(i), this);
            pipelines.add(fsm);
            map.put(states.get(i), fsm);
        }
    }

    void doIt(FFF.FsmContext ctx) throws FlowEndException {
        if (null == ctx.getState()) {
            ctx.state    = pipelines.get(0).state;
            ctx.subState = FFF.SubState.task;
        }
        map.get(ctx.getState()).onEvent(ctx);
    }

    public static void main(String[] args) {
        FsmPipeline p = new FsmPipeline(Arrays.stream(Pay.values()).collect(Collectors.toList()));
        try {
            p.doIt(new FFF.FsmContext());
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
