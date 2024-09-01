package cn.hz.ddbm.pc.chaos

import cn.hutool.core.util.ClassUtil
import cn.hutool.core.util.EnumUtil
import cn.hutool.core.util.ReflectUtil
import cn.hutool.json.JSONUtil
import cn.hz.ddbm.pc.newcore.utils.ExpressionEngineUtils
import lombok.Data
import org.junit.Test
import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.expression.ExpressionParser
import org.springframework.expression.spel.standard.SpelExpressionParser
import org.springframework.scripting.groovy.GroovyScriptEvaluator
import org.springframework.scripting.groovy.GroovyScriptFactory

import static org.mockito.Mockito.*

class RuleTest {

    @Before
    void setUp() {
    }

    @Test
    void testToValue() {
        Rule rule = new Rule(
                weight: 0.1,
                value: 'F',
                value_type: 'cn.hz.ddbm.pc.chaos.TF'
        )
        rule.toValue();
        rule.toWeight()


          rule = new Rule(
                weight: 0.1,
                value: '',
                value_type: 'cn.hz.ddbm.pc.chaos.TF'
        )
        rule.toValue();
        rule.toWeight()
    }

    @Test
    void testToWeight() {
//        print(JSONUtil.toBean("{}",TF.class))
        Class<Enum> tf = ClassUtil.forName("cn.hz.ddbm.pc.chaos.TF")

        print(Enum.valueOf(tf, "F"))
        print(JSONUtil.toJsonStr(new StateTest(tf: TF.T)))
    }

    @Data
    static class StateTest {
        TF tf;
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme