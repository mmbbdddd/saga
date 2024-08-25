package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;

public class ParallelAction<S extends Enum<S>> extends MultiAction<S>{

    public ParallelAction(String actionNames, List<Action<S>> actions) {
        super(actionNames, actions);
    }

    @Override
    public void execute(FsmContext<S, ?> ctx) throws Exception {

    }

    @Override
    public S query(FsmContext<S, ?> ctx) throws Exception {
        return null;
    }
}
