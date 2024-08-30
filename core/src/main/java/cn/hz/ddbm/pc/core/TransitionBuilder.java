package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.processor.saga.SagaTransitionBuilder;

import java.util.Collection;
import java.util.List;

public interface TransitionBuilder <S> {
    List<Transition > build();
}
