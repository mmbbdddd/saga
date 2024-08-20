package cn.hz.ddbm.pc.configuration;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.container.ChaosAspect;
import cn.hz.ddbm.pc.container.chaos.ChaosHandler;
import cn.hz.ddbm.pc.core.action.ChaosAction;
import cn.hz.ddbm.pc.core.support.Locker;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;
import cn.hz.ddbm.pc.core.utils.InfraUtils;
import cn.hz.ddbm.pc.lock.JdkLocker;
import cn.hz.ddbm.pc.profile.ChaosPcService;
import cn.hz.ddbm.pc.profile.PcService;
import cn.hz.ddbm.pc.session.memory.MemorySessionManager;
import cn.hz.ddbm.pc.statistics.SimpleStatistics;
import cn.hz.ddbm.pc.status.memory.MemoryStatusManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@ConditionalOnClass({PcService.class})
@EnableConfigurationProperties({PcProperties.class})
@EnableAspectJAutoProxy
public class PcChaosConfiguration {


    @Bean
    ChaosAction chaosAction() {
        return new ChaosAction();
    }

    @Bean
    ChaosPcService pcService() {
        return new ChaosPcService();
    }

    @Bean
    MemoryStatusManager memoryStatusManager() {
        return new MemoryStatusManager(256, 1);
    }

    @Bean
    MemorySessionManager memorySessionManager() {
        return new MemorySessionManager(256, 1);
    }

    @Bean
    ChaosHandler chaosHandler(ChaosPcService pcService) {
        return new ChaosHandler(pcService);
    }

    @Bean
    ChaosAspect aspect() {
        return new ChaosAspect();
    }


    @Bean
    Locker locker() {
        return new JdkLocker();
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
    StatisticsSupport statisticsSupport(PcProperties properties) {
        return new SimpleStatistics(properties.statistics.cacheSize, properties.getStatistics().hours);
    }


}
