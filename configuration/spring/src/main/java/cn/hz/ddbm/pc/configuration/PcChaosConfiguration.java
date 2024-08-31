//package cn.hz.ddbm.pc.configuration;
//
//import cn.hutool.extra.spring.SpringUtil;
//import cn.hz.ddbm.pc.container.ChaosAspect;
//import cn.hz.ddbm.pc.container.chaos.ChaosHandler;
//import cn.hz.ddbm.pc.core.support.Locker;
//import cn.hz.ddbm.pc.core.support.StatisticsSupport;
//import cn.hz.ddbm.pc.core.utils.InfraUtils;
//import cn.hz.ddbm.pc.lock.JdkLocker;
//import cn.hz.ddbm.pc.profile.BaseService;
//import cn.hz.ddbm.pc.profile.ChaosSagaService;
//import cn.hz.ddbm.pc.session.memory.MemorySessionManager;
//import cn.hz.ddbm.pc.statistics.SimpleStatistics;
//import cn.hz.ddbm.pc.status.memory.MemoryStatusManager;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.EnableAspectJAutoProxy;
//
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.ScheduledThreadPoolExecutor;
//
//@ConditionalOnClass({BaseService.class})
//@EnableConfigurationProperties({PcProperties.class})
//@EnableAspectJAutoProxy
//public class PcChaosConfiguration {
//
//
//    @Bean
//    ExecutorService pluginExecutorService() {
//        return new ScheduledThreadPoolExecutor(10);
//    }
//
//    @Bean
//    ExecutorService actionExecutorService() {
//        return new ScheduledThreadPoolExecutor(10);
//    }
//
//    @Bean
//    ChaosSagaService pcService() {
//        return new ChaosSagaService();
//    }
//
//    @Bean
//    MemoryStatusManager memoryStatusManager() {
//        return new MemoryStatusManager(256, 1);
//    }
//
//    @Bean
//    MemorySessionManager memorySessionManager() {
//        return new MemorySessionManager(256, 1);
//    }
//
//    @Bean
//    ChaosHandler chaosHandler(ChaosSagaService pcService) {
//        return new ChaosHandler(pcService);
//    }
//
//    @Bean
//    ChaosAspect aspect() {
//        return new ChaosAspect();
//    }
//
//
//    @Bean
//    Locker locker() {
//        return new JdkLocker();
//    }
//
//
//    @Bean
//    InfraUtils infraUtils() {
//        return new InfraUtils();
//    }
//
//    @Bean
//    SpringUtil springUtil() {
//        return new SpringUtil();
//    }
//
//    @Bean
//    StatisticsSupport statisticsSupport(PcProperties properties) {
//        return new SimpleStatistics(properties.statistics.cacheSize, properties.getStatistics().hours);
//    }
//
//
//}
