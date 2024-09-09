package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.Payload;


public interface FsmPayload<S extends Enum<S>> extends Payload<FsmState<S>> {

}
