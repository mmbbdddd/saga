package cn.hz.ddbm.pc.factory.saga;

import cn.hz.ddbm.pc.newcore.saga.SagaFlow;

public interface SAGA<S extends Enum<S>> {
    default SagaFlow<S> build() {
        return null;
    }

}
