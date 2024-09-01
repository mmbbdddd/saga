package cn.hz.ddbm.pc.support;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import cn.hz.ddbm.pc.newcore.Profile;
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
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
import cn.hz.ddbm.pc.newcore.saga.SagaPayload;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;

import javax.annotation.Resource;
import java.util.List;

public abstract class BaseService {
    @Resource
    SagaProcessor<?> sagaProcessor;
    @Resource
    FsmProcessor<?>  fsmProcessor;

    public void batchFsm(String flowName, List<FsmPayload> payloads) {
        for (FsmPayload payload : payloads) {
            try {
                fsmFlow(flowName, payload, Coast.FSM.EVENT_DEFAULT);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    public void batchSaga(String flowName, List<SagaPayload> payloads) {
        for (SagaPayload payload : payloads) {
            try {
                sagaFlow(flowName, payload);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    public void sagaFlow(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        SagaContext ctx = sagaProcessor.workerProcess(flowName, payload);
        sagaProcessor.flowProcess(ctx);
    }

    public void saga(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        sagaProcessor.workerProcess(flowName, payload);
    }

    public void fsmFlow(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        FsmContext ctx = fsmProcessor.workerProcess(flowName, payload, event);
        fsmProcessor.flowProcess(ctx);
    }

    public void fsm(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        fsmProcessor.workerProcess(flowName, payload, event);
    }
}
