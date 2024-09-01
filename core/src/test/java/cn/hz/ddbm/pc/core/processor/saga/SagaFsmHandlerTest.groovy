package cn.hz.ddbm.pc.core.processor.saga

import cn.hz.ddbm.pc.core.FsmContext
import org.junit.Before
import org.junit.Test
import org.mockito.Mock

import static org.mockito.Mockito.*

class SagaFsmHandlerTest {
    @Mock
    SagaFsmHandler.ForwardQuantum forward
    @Mock
    SagaFsmHandler.BackoffQuantum backoff

    SagaFsmHandler sagaFsmHandler;

    @Before
    void setUp() {
        sagaFsmHandler = new SagaFsmHandler(null, null, null, null, null, null, null)
    }

    @Test
    void testOnEvent() {
        sagaFsmHandler.onEvent(new SagaProcessor(null, [null]), new FsmContext(null, null, "event", null))
        verify(forward).onEvent(any(SagaProcessor.class), any(FsmContext.class))
        verify(backoff).onEvent(any(SagaProcessor.class), any(FsmContext.class
        ))
    }

    @Test
    void testToTransitions() {
//        when(forward.getTask()).thenReturn(new S())
//        when(forward.getFailover()).thenReturn(new S())
//        when(backoff.getTask()).thenReturn(new S())
//        when(backoff.getFailover()).thenReturn(new S())
//
//        Set<Transition> result = sagaFsmHandler.toTransitions()
//        assert result == [new Transition(ProcessorType.
//                FSM, null, "event", "actionDsl", new SagaFsmHandler(null, null, null, null, null, null, null))] as Set<Transition>
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme