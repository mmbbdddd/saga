package cn.hz.ddbm.pc.newcore.saga;

import cn.hz.ddbm.pc.newcore.Payload;

public interface SagaPayload<S extends Enum<S>> extends Payload<SagaState<S>> {

}
