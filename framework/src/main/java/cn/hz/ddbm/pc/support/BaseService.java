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
    protected SagaProcessor sagaProcessor;
    @Resource
    protected FsmProcessor  fsmProcessor;

    protected void batchSAGAs(String flowName, List<SagaPayload> payloads) {
        for (SagaPayload payload : payloads) {
            try {
                executeSAGAs(flowName, payload);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    protected void batchFSMs(String flowName, List<FsmPayload> payloads) {
        for (FsmPayload payload : payloads) {
            try {
                executeFSMs(flowName, payload, Coast.FSM.EVENT_DEFAULT);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    protected void executeSAGAs(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        SagaContext ctx = sagaProcessor.getContext(flowName, payload);
        sagaProcessor.flowProcess(ctx);
    }

    protected void executeSAGA(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        SagaContext ctx = sagaProcessor.getContext(flowName, payload);
        sagaProcessor.workerProcess(ctx);
    }

    protected void executeFSMs(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        FsmContext ctx = fsmProcessor.getContext(flowName, payload);
        fsmProcessor.workerProcess(event, ctx);
        fsmProcessor.flowProcess(ctx);
    }

    protected void executeFSM(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        FsmContext ctx = fsmProcessor.getContext(flowName, payload);
        fsmProcessor.workerProcess(event, ctx);
    }
}
