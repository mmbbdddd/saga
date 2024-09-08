package cn.hz.ddbm.pc.chaos.config;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.chaos.support.ChaosHandler;
import cn.hz.ddbm.pc.chaos.support.LocalChaosAction;
import cn.hz.ddbm.pc.chaos.support.RemoteChaosAction;
import cn.hz.ddbm.pc.factory.fsm.BeanFsmFlowFactory;
import cn.hz.ddbm.pc.factory.saga.BeanSagaFlowFactory;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;


@EnableAspectJAutoProxy
public class ChaosConfiguration {

    @Bean
    RemoteChaosAction remoteChaosAction() {
        return new RemoteChaosAction();
    }

    @Bean
    LocalChaosAction localChaosAction() {
        return new LocalChaosAction();
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

