package cn.hz.ddbm.pc.example;

import cn.hz.ddbm.pc.configuration.PcChaosConfiguration;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.profile.ChaosPcService;
import cn.hz.ddbm.pc.profile.chaos.ChaosRule;
import cn.hz.ddbm.pc.profile.chaos.ChaosTarget;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@ComponentScan("cn.hz.ddbm.pc.example.actions")
@SpringBootTest
@Import({PcChaosConfiguration.class, PayTest.DemoConfig.class})
@RunWith(SpringRunner.class)
public class PayTest {


    @Autowired
    ChaosPcService chaosService;

    /**
     * doc/img_4.png
     */

    @Test
    public void chaos() throws Exception {
        String event = Coasts.EVENT_DEFAULT;
        List<ChaosRule> rules = new ArrayList<ChaosRule>() {{
            //注入业务逻辑异常，概率20%
//            add(new ChaosRule(ChaosTarget.ACTION, "true", "action异常", 0.1, new ArrayList<Class<? extends Throwable>>() {{
//                add(RuntimeException.class);
//                add(Exception.class);
//            }}));
            //注入锁错误
//            add(new ChaosRule(ChaosTarget.LOCK, "true", "锁异常", 0.1, new ArrayList<Class<? extends Throwable>>() {{
//                add(RuntimeException.class);
//                add(Exception.class);
//            }}));
        }};
        try {
            //执行100此，查看流程中断概率
            chaosService.execute("test", new ChaosPcService.MockPayLoad(PayState.init), event, 1000, 10, rules, true);
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

        String event = Coasts.EVENT_DEFAULT;
        try {
            //执行10000次，查看流程中断概率
            chaosService.execute("test", new ChaosPcService.MockPayLoad(PayState.init), event, 10000, 10, new ArrayList(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("account:" + account.get());
        System.out.println("freezed:" + freezed.get());
        System.out.println("bank:" + bank.get());
    }


    public static class DemoConfig {
        @Bean
        PayFsm pcConfig() {
            return new PayFsm();
        }
    }


}
