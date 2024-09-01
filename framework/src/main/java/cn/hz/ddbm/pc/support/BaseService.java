package cn.hz.ddbm.pc.support;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.PauseException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmPayload;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.saga.SagaPayload;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;

import javax.annotation.Resource;
import java.util.List;

public abstract class BaseService {
    @Resource
    protected SagaProcessor<?> sagaProcessor;
    @Resource
    protected FsmProcessor<?>  fsmProcessor;

    protected void batchFsms(String flowName, List<FsmPayload> payloads) {
        for (FsmPayload payload : payloads) {
            try {
                fsms(flowName, payload, Coast.FSM.EVENT_DEFAULT);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    protected void batchSagas(String flowName, List<SagaPayload> payloads) {
        for (SagaPayload payload : payloads) {
            try {
                sagas(flowName, payload);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    protected void sagas(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        SagaContext ctx = sagaProcessor.workerProcess(flowName, payload);
        sagaProcessor.flowProcess(ctx);
    }

    protected void saga(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        sagaProcessor.workerProcess(flowName, payload);
    }

    protected void fsms(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        FsmContext ctx = fsmProcessor.workerProcess(flowName, payload, event);
        fsmProcessor.flowProcess(ctx);
    }

    protected void fsm(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        fsmProcessor.workerProcess(flowName, payload, event);
    }
}
