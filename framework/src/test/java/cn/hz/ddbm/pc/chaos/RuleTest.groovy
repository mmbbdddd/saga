package cn.hz.ddbm.pc.chaos

import cn.hutool.core.util.ClassUtil
import cn.hutool.json.JSONUtil
import lombok.Data
import org.junit.Before
import org.junit.Test

class RuleTest {

    @Before
    void setUp() {
    }

    @Test
    void testToValue() {
        ChaosRule rule = new ChaosRule(
                weight: 0.1,
                value: 'F',
                value_type: 'cn.hz.ddbm.pc.chaos.TF'
        )
        rule.toValue();
        rule.toWeight()


        rule = new ChaosRule(
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