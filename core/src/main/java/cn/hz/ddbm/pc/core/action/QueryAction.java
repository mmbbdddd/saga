package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;

public interface QueryAction<S extends State> extends Action  {
    S queryState(FsmContext  ctx) throws Exception;


}