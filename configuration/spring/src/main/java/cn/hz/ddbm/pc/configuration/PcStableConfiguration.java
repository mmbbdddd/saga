//package cn.hz.ddbm.pc.configuration;
//
//import cn.hz.ddbm.pc.support.BaseService;
//import cn.hz.ddbm.pc.profile.Sagaervice;
//import cn.hz.ddbm.pc.session.memory.MemorySessionManager;
//import cn.hz.ddbm.pc.session.redis.RedisSessionManager;
//import cn.hz.ddbm.pc.status.memory.MemoryStatusManager;
//import cn.hz.ddbm.pc.status.redis.RedisStatusManager;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
//import org.springframework.boot.context.properties.EnableConfigurationProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.data.redis.core.RedisTemplate;
//
//@ConditionalOnClass({BaseService.class})
//@EnableConfigurationProperties({PcProperties.class})
//public class PcStableConfiguration {
//
//    @Bean
//    Sagaervice pcService() {
//        return new Sagaervice();
//    }
//
//    @Bean
//    MemoryStatusManager memoryStatusManager(PcProperties properties) {
//        return new MemoryStatusManager(properties.statusManager.cacheSize, properties.getStatusManager().hours);
//    }
//
//    @Bean
//    @ConditionalOnBean(RedisTemplate.class)
//    RedisStatusManager redisStatusManager() {
//        return new RedisStatusManager();
//    }
//
//    @Bean
//    MemorySessionManager memorySessionManager(PcProperties properties) {
//        return new MemorySessionManager(properties.sessionManager.cacheSize, properties.sessionManager.hours);
//    }
//
//    @Bean
//    @ConditionalOnBean(RedisTemplate.class)
//    RedisSessionManager redisSessionManager() {
//        return new RedisSessionManager();
//    }
//}
