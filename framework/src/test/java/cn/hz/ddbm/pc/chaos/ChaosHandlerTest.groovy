package cn.hz.ddbm.pc.chaos

import cn.hutool.core.lang.Pair
import cn.hutool.core.text.csv.CsvReader
import cn.hutool.core.text.csv.CsvUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.setting.Setting
import cn.hz.ddbm.pc.ChaosService
import cn.hz.ddbm.pc.chaos.actions.TestFsmAction
import cn.hz.ddbm.pc.newcore.fsm.FsmContext
import cn.hz.ddbm.pc.newcore.saga.SagaContext
import cn.hz.ddbm.pc.newcore.test.NoneSagaAction
import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils
import org.junit.Test
import org.junit.Before
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner

import javax.annotation.Resource
import java.lang.reflect.Method
import java.nio.charset.Charset

import static org.mockito.Mockito.*

@SpringBootTest
@RunWith(SpringRunner)
@Import(TestConfig.class)
class ChaosHandlerTest {
    @Mock
    ChaosService chaosService
    @Resource
    ChaosHandler chaosHandler

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
                 Object ss  =  action.executeQuery(null)
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