package cn.hz.ddbm.pc;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.chaos.ChaosAction;
import cn.hz.ddbm.pc.chaos.ChaosHandler;
import cn.hz.ddbm.pc.chaos.ChaosRule;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ChaosService extends BaseService {
    @Autowired
    ChaosHandler chaosHandler;

    public void batchFsms(String flowName, List<FsmPayload> payloads, List<ChaosRule> rules) {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        chaosHandler.setChaosRules(rules);
        super.batchFsms(flowName, payloads);
    }


    public void batchSagas(String flowName, List<SagaPayload> payloads, List<ChaosRule> rules) {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        chaosHandler.setChaosRules(rules);
        super.batchSagas(flowName, payloads);
    }


    public void sagas(String flowName, SagaPayload payload, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        chaosHandler.setChaosRules(rules);
        super.sagas(flowName, payload);
    }


    public void saga(String flowName, SagaPayload payload, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        chaosHandler.setChaosRules(rules);
        super.saga(flowName, payload);
    }


    public void fsms(String flowName, FsmPayload payload, String event,Boolean mockBean, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        if(mockBean) {
            this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
            this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        }
        chaosHandler.setChaosRules(rules);
        super.fsms(flowName, payload, event);
    }


    public void fsm(String flowName, FsmPayload payload, String event, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        this.sagaProcessor.runMode = FlowProcessorService.RunMode.chaos;
        this.fsmProcessor.runMode  = FlowProcessorService.RunMode.chaos;
        chaosHandler.setChaosRules(rules);
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



