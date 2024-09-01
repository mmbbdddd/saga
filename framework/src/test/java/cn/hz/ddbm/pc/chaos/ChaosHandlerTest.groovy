package cn.hz.ddbm.pc.chaos


import cn.hz.ddbm.pc.ChaosService
import cn.hz.ddbm.pc.chaos.actions.TestFsmAction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner

import javax.annotation.Resource

@SpringBootTest
@RunWith(SpringRunner)
@Import(TestConfig.class)
class ChaosHandlerTest {
    @Mock
    ChaosService chaosService
    @Resource
    ChaosHandlerImpl chaosHandler

    @Resource
    TestFsmAction action;

    @Before
    void setUp() {
    }

    @Test
    void testHandle() {
//        chaosHandler.initRules();
        chaosHandler.initRules()
        10.times {
            try {
                Object ss = action.executeQuery(null)
                println ss;
                int cc = 0;
            } catch (Exception ee) {
                println("sssss" + ee.getMessage())
            }
        }

    }

    @Test
    void testGenerateResult() {

    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme