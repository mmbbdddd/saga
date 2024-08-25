package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;

import java.util.List;
import java.util.stream.Collectors;

public class SerialAction<S extends Enum<S>> extends MultiAction  {

    public SerialAction(String actionNames, List<Action > actions) {
        super(actionNames, actions);
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {

    }

    @Override
    public Enum query(FsmContext ctx) throws Exception {
        return null;
    }

    @Override
    public Enum failover() {
        return null;
    }


}
