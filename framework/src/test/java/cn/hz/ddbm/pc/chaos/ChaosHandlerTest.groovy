package cn.hz.ddbm.pc.chaos

import cn.hutool.core.lang.Pair
import cn.hutool.core.util.ReflectUtil
import cn.hz.ddbm.pc.ChaosService
import cn.hz.ddbm.pc.newcore.fsm.FsmContext
import cn.hz.ddbm.pc.newcore.saga.SagaContext
import cn.hz.ddbm.pc.newcore.test.NoneSagaAction
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

import static org.mockito.Mockito.*

@SpringBootTest
@RunWith(SpringRunner)
@Import(TestConfig.class)
class ChaosHandlerTest {
    @Mock
    ChaosService chaosService
    @Resource
    ChaosHandler chaosHandler

    @Before
    void setUp() {
    }

    @Test
    void testHandle() {
        when(chaosService.chaosRules()).thenReturn([new ChaosRule(ChaosTargetType.sagaAction, "expression", "errorMessage", 0d, [null])])

        chaosHandler.handle(ChaosTargetType.sagaAction, "proxy", null, ["args"] as Object[])
    }

    @Test
    void testGenerateResult() {
        when(chaosService.getFsmSagaRules()).thenReturn([new Pair<String, Object>("key", "value")])
        Method method = ReflectUtil.getMethod(NoneSagaAction.class,"executeQuery", SagaContext.class );
        Object result = chaosHandler.generateResult(ChaosTargetType.sagaAction, "target", method, ["args"] as Object[])
        assert [true,false,null].contains(result)
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme