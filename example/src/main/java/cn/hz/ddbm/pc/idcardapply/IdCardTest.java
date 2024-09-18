package cn.hz.ddbm.pc.idcardapply;

import cn.hz.ddbm.pc.chaos.ChaosService;
import cn.hz.ddbm.pc.chaos.config.ChaosConfiguration;
import cn.hz.ddbm.pc.chaos.support.ChaosConfig;
import cn.hz.ddbm.pc.idcardapply.actions.MaterialCollectionAction;
import cn.hz.ddbm.pc.idcardapply.actions.RuleCheckedAction;
import cn.hz.ddbm.pc.idcardapply.actions.SendBizAction;
import cn.hz.ddbm.pc.plugin.PerformancePlugin;
import cn.hz.ddbm.pc.saga.PayTest;
import cn.hz.ddbm.pc.saga.actions.SagaEndAction;
import cn.hz.ddbm.pc.saga.actions.SagaFreezeAction;
import cn.hz.ddbm.pc.saga.actions.SagaPayAction;
import cn.hz.ddbm.pc.saga.actions.SagaSendAction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@ComponentScan("cn.hz.ddbm.pc.idcardapply.actions")
@SpringBootTest
@Import({IdCardTest.CC.class, ChaosConfiguration.class})
@RunWith(SpringRunner.class)
public class IdCardTest {
    @Autowired
    ChaosService chaosService;
    ChaosConfig chaosConfig = ChaosConfig.goodOf();

    @Test
    public void chaos() throws Exception {

        try {
            //执行100此，查看流程中断概率
            chaosService.fsm("idcard",  false,IdCardFSM.MaterialCollection,3, 1, 4, chaosConfig );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class CC {
        @Bean
        MaterialCollectionAction materialCollectionAction(){
            return new MaterialCollectionAction();
        }
        @Bean
        RuleCheckedAction ruleCheckedAction(){
            return new RuleCheckedAction();
        }
        @Bean
        SendBizAction sendBizAction(){
            return new SendBizAction();
        }

        @Bean
        IdCardFlow idCard(){
            return new IdCardFlow();
        }

        @Bean
        ChaosService chaosService() {
            return new ChaosService();
        }

    }
}
