package cn.hz.ddbm.pc.core.processor.saga;

import cn.hz.ddbm.pc.core.Transition;
import cn.hz.ddbm.pc.core.TransitionBuilder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SagaTransitionBuilder<S> implements TransitionBuilder<S> {

    Collection<InnerTempTransition> transitions;

    private SagaTransitionBuilder() {
        this.transitions = new ArrayList<>();
    }

    public static <S> SagaTransitionBuilder<S> newBuilder(Class<S> type) {
        return new SagaTransitionBuilder<>();
    }


    public SagaTransitionBuilder<S> saga(S task, S pre, String sagaAction, Integer retry) {
        this.transitions.add(new InnerTempTransition(new SagaState<>(task), new SagaState<>(pre), sagaAction, retry));
        return this;
    }

    @Override
    public List<Transition> build() {
        List<SagaFsmHandler> list = new ArrayList<>();
        for (InnerTempTransition t : transitions) {
            SagaFsmHandler s = new SagaFsmHandler(
                    t.getState(), t.getFailover(), t.getSu(), null, null, null, t.getSagaAction()
            );
            list.add(s);
        }
        return list.stream().map(SagaFsmHandler::toTransitions).flatMap(Collection::stream).collect(Collectors.toList());
    }


    @Data
    static class InnerTempTransition<S> {
        SagaState<S> state;
        SagaState<S> failover;
        SagaState<S> su;
        SagaState<S> rollback;
        SagaState<S> rollbackFailover;
        SagaState<S> pre;
        String       sagaAction;
        Integer      retry;

        public InnerTempTransition(SagaState<S> task, SagaState<S> pre, String sagaAction, Integer retry) {
            this.state            = task.task();
            this.failover         = task.task();
            this.su               = task.su();
            this.rollback         = task.rollback();
            this.rollbackFailover = task.rollbackFailover();
            this.pre              = pre.task();
            this.sagaAction       = sagaAction;
            this.retry            = retry;
        }

//        public InnerTempTransition(String state, String sagaAction, String failover, String su, String fail, String rollbackFailover, String pre) {
//            this.state            = state;
//            this.failover         = failover;
//            this.su               = su;
//            this.fail             = fail;
//            this.rollbackFailover = rollbackFailover;
//            this.pre              = pre;
//            this.sagaAction       = sagaAction;
//        }

    }
}
