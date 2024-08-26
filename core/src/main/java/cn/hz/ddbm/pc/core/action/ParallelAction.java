package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;

public class ParallelAction extends MultiAction{

    public ParallelAction(String actionNames,Enum failover, List<Action> actions) {
        super(actionNames,failover, actions);
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {

    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return null;
    }

}
