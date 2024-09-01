package cn.hz.ddbm.pc;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.chaos.AopAspect;
import cn.hz.ddbm.pc.chaos.ChaosAction;
import cn.hz.ddbm.pc.chaos.ChaosHandler;
import cn.hz.ddbm.pc.factory.fsm.BeanFsmFlowFactory;
import cn.hz.ddbm.pc.factory.saga.BeanSagaFlowFactory;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.PauseException;
import cn.hz.ddbm.pc.newcore.exception.SessionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmPayload;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.saga.SagaPayload;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;
import cn.hz.ddbm.pc.support.BaseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ChaosService extends BaseService {
    @Override
    public void batchFsms(String flowName, List<FsmPayload> payloads) {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        super.batchFsms(flowName, payloads);
    }

    @Override
    public void batchSagas(String flowName, List<SagaPayload> payloads) {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        super.batchSagas(flowName, payloads);
    }

    @Override
    public void sagas(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        super.sagas(flowName, payload);
    }

    @Override
    public void saga(String flowName, SagaPayload payload) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        super.saga(flowName, payload);
    }

    @Override
    public void fsms(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        super.fsms(flowName, payload, event);
    }

    @Override
    public void fsm(String flowName, FsmPayload payload, String event) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        super.fsm(flowName, payload, event);
    }

    @ConditionalOnClass({BaseService.class})
    @EnableAspectJAutoProxy
   public static   class ChaosConfiguration {

        @Bean
        ChaosAction chaosAction(){
            return new ChaosAction();
        }
        @Bean
        BeanFsmFlowFactory fsmFlowFactory() {
            return new BeanFsmFlowFactory();
        }

        @Bean
        BeanSagaFlowFactory sagaFlowFactory() {
            return new BeanSagaFlowFactory();
        }

        @Bean
        ExecutorService actionExecutorService() {
            return new ScheduledThreadPoolExecutor(10);
        }

        @Bean
        FsmProcessor fsmProcessor() {
            return new FsmProcessor();
        }

        @Bean
        SagaProcessor sagaProcessor() {
            return new SagaProcessor();
        }


        @Bean
        JvmStatusManager jvmStatusManager() {
            return new JvmStatusManager();
        }

        @Bean
        JvmSessionManager jvmSessionManager() {
            return new JvmSessionManager();
        }

        @Bean
        ChaosHandler chaosHandler() {
            return new ChaosHandler();
        }

        @Bean
        AopAspect aspect() {
            return new AopAspect();
        }


        @Bean
        JvmLocker jvmLocker() {
            return new JvmLocker();
        }


        @Bean
        InfraUtils infraUtils() {
            return new InfraUtils();
        }

        @Bean
        SpringUtil springUtil() {
            return new SpringUtil();
        }

        @Bean
        StatisticsSupport statisticsSupport() {
            return new JvmStatisticsSupport();
        }
    }
}



