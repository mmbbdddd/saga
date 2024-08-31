package cn.hz.ddbm.pc.profile;

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

    public void batchFsm(String flowName, List<FsmPayload> payloads, Profile profile) {
        for (FsmPayload payload : payloads) {
            try {
                fsmFlow(flowName, payload, Coast.FSM.EVENT_DEFAULT,profile);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    public void batchSaga(String flowName, List<SagaPayload> payloads,Profile profile) {
        for (SagaPayload payload : payloads) {
            try {
                sagaFlow(flowName, payload,profile);
            } catch (Exception e) {
                Logs.error.error("", e);
            }
        }
    }

    public void sagaFlow(String flowName, SagaPayload payload, Profile profile) throws PauseException, SessionException, FlowEndException, InterruptedException {
        SagaContext ctx = sagaProcessor.workerProcess(flowName, payload, profile);
        sagaProcessor.flowProcess(ctx);
    }

    public void saga(String flowName, SagaPayload payload, Profile profile) throws PauseException, SessionException, FlowEndException, InterruptedException {
        sagaProcessor.workerProcess(flowName, payload, profile);
    }

    public void fsmFlow(String flowName, FsmPayload payload, String event, Profile profile) throws PauseException, SessionException, FlowEndException, InterruptedException {
        FsmContext ctx = fsmProcessor.workerProcess(flowName, payload, event, profile);
        fsmProcessor.flowProcess(ctx);
    }

    public void fsm(String flowName, FsmPayload payload, String event, Profile profile) throws PauseException, SessionException, FlowEndException, InterruptedException {
        fsmProcessor.workerProcess(flowName, payload, event, profile);
    }
}
