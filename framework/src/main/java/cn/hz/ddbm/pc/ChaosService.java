package cn.hz.ddbm.pc;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.chaos.ChaosAction;
import cn.hz.ddbm.pc.chaos.ChaosHandlerImpl;
import cn.hz.ddbm.pc.chaos.ChaosRule;
import cn.hz.ddbm.pc.factory.fsm.BeanFsmFlowFactory;
import cn.hz.ddbm.pc.factory.saga.BeanSagaFlowFactory;
import cn.hz.ddbm.pc.newcore.config.Coast;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


public class ChaosService extends BaseService {
    @Autowired
    ChaosHandlerImpl chaosHandler;

    public void batchFSMs(String flowName, List<FsmPayload> payloads, List<ChaosRule> rules) {
        System.setProperty(Coast.RUN_MODE,Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        super.batchFSMs(flowName, payloads);
    }


    public void batchSAGAs(String flowName, List<SagaPayload> payloads, List<ChaosRule> rules) {
        System.setProperty(Coast.RUN_MODE,Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        super.batchSAGAs(flowName, payloads);
    }


    public void executeSAGAs(String flowName, SagaPayload payload, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        System.setProperty(Coast.RUN_MODE,Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        super.executeSAGAs(flowName, payload);
    }


    public void executeSAGA(String flowName, SagaPayload payload, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        System.setProperty(Coast.RUN_MODE,Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        super.executeSAGA(flowName, payload);
    }


    public void executeFSMs(String flowName, FsmPayload payload, String event, Boolean mockBean, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        System.setProperty(Coast.RUN_MODE,Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        super.executeFSMs(flowName, payload, event);
    }


    public void executeFSM(String flowName, FsmPayload payload, String event, List<ChaosRule> rules) throws PauseException, SessionException, FlowEndException, InterruptedException {
        System.setProperty(Coast.RUN_MODE,Coast.RUN_MODE_CHAOS);
        chaosHandler.setChaosRules(rules);
        super.executeFSM(flowName, payload, event);
    }

    @ConditionalOnClass({BaseService.class})
    @EnableAspectJAutoProxy
    public static class ChaosConfiguration {

        @Bean
        ChaosAction chaosAction() {
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
        ChaosHandlerImpl chaosHandler() {
            return new ChaosHandlerImpl();
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



