package cn.hz.ddbm.pc.fsm;

import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ChaosService;
import cn.hz.ddbm.pc.chaos.ChaosRule;
import cn.hz.ddbm.pc.chaos.ChaosRuleType;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.chaos.ChaosTargetType;
import cn.hz.ddbm.pc.newcore.fsm.FsmPayload;
import cn.hz.ddbm.pc.plugin.PerformancePlugin;
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

@ComponentScan("cn.hz.ddbm.pc.actions")
@SpringBootTest
@Import({PayTest.CC.class, ChaosService.ChaosConfiguration.class})
@RunWith(SpringRunner.class)
public class PayTest {


    @Autowired
    ChaosService chaosService;

    /**
     * doc/img_4.png
     */

    @Test
    public void chaos() throws Exception {
        List<ChaosRule> rules = new ArrayList<ChaosRule>() {{
            this.add(new ChaosRule(ChaosRuleType.EXCEPTION, RuntimeException.class, 0.1));
        }};
        try {
            //执行100此，查看流程中断概率
            chaosService.executeFSMs("test", new FsmPayload(1, FlowStatus.INIT, PayState.init), null, true, rules);
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
            //执行10000次，查看流程中断概率
            chaosService.executeFSMs("test", new FsmPayload(1, FlowStatus.INIT, PayState.init), null, false, null);
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
        ChaosService chaosService() {
            return new ChaosService();
        }

        @Bean
        PayFsm test() {
            return new PayFsm();
        }

        @Bean
        PerformancePlugin performancePlugin() {
            return new PerformancePlugin();
        }
    }


}
