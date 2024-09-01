package cn.hz.ddbm.pc.chaos;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ChaosService;
import cn.hz.ddbm.pc.newcore.fsm.FsmProcessor;
import cn.hz.ddbm.pc.newcore.infra.InfraUtils;
import cn.hz.ddbm.pc.newcore.infra.StatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmLocker;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmSessionManager;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatisticsSupport;
import cn.hz.ddbm.pc.newcore.infra.impl.JvmStatusManager;
import cn.hz.ddbm.pc.newcore.saga.SagaProcessor;
import cn.hz.ddbm.pc.support.BaseService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@ConditionalOnClass({BaseService.class})
@EnableAspectJAutoProxy( proxyTargetClass = true)
public class TestConfig {

    @Bean
    ExecutorService actionExecutorService() {
        return new ScheduledThreadPoolExecutor(10);
    }

    @Bean
    FsmProcessor fsmProcessor(){
        return new FsmProcessor();
    }
    @Bean
    SagaProcessor sagaProcessor(){
        return new SagaProcessor();
    }

    @Bean
    ChaosService chaosService() {
        return new ChaosService();
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