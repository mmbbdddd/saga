package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;

public interface CommandAction<S extends State> extends Action {

    S execute(FsmContext ctx) throws Exception;

}