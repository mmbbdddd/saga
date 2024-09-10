package cn.hz.ddbm.pc.saga;

import cn.hutool.core.lang.Pair;
import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.chaos.ChaosService;
import cn.hz.ddbm.pc.chaos.config.ChaosConfiguration;
import cn.hz.ddbm.pc.chaos.support.ChaosConfig;
import cn.hz.ddbm.pc.newcore.chaos.ChaosRule;
import cn.hz.ddbm.pc.plugin.PerformancePlugin;
import cn.hz.ddbm.pc.saga.actions.SagaEndAction;
import cn.hz.ddbm.pc.saga.actions.SagaFreezeAction;
import cn.hz.ddbm.pc.saga.actions.SagaPayAction;
import cn.hz.ddbm.pc.saga.actions.SagaSendAction;
import com.google.common.collect.Sets;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ComponentScan("cn.hz.ddbm.pc.actions")
@SpringBootTest
@Import({PayTest.CC.class, ChaosConfiguration.class})
@RunWith(SpringRunner.class)
public class PayTest {


    @Autowired
    ChaosService chaosService;
    ChaosConfig chaosConfig = ChaosConfig.goodOf();

    /**
     * doc/img_4.png
     */

    @Test
    public void chaos() throws Exception {

        try {
            //执行100此，查看流程中断概率
            chaosService.saga("test",  true,3, 1, 4, chaosConfig );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //模拟支付账号
    public static AtomicInteger account;
    //    模拟冻结字段
    public static AtomicInteger freezed;
    //    模拟收款账号
    public static AtomicInteger bank;

    @Test
    public void acid() throws Exception {
        account = new AtomicInteger(10000);
        freezed = new AtomicInteger(0);
        bank    = new AtomicInteger(0);

        try {
            //执行1000次，查看流程中断概率
            chaosService.saga("test", false,1, 1, 10, ChaosConfig.goodOf());

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("account:" + account.get());
        System.out.println("freezed:" + freezed.get());
        System.out.println("bank:" + bank.get());
        SpringUtil.publishEvent(new PerformancePlugin.Event());
    }

    public static class CC {
        @Bean
        SagaFreezeAction sagaFreezeAction(){
            return new SagaFreezeAction();
        }
        @Bean
        SagaEndAction sagaEndAction(){
            return new SagaEndAction();
        }
        @Bean
        SagaPayAction sagaPayAction(){
            return new SagaPayAction();
        }
        @Bean
        SagaSendAction sagaSendAction(){
            return new SagaSendAction();
        }
        @Bean
        ChaosService chaosService() {
            return new ChaosService();
        }


        @Bean
        PerformancePlugin performancePlugin() {
            return new PerformancePlugin();
        }
    }


}
