package cn.hz.ddbm.pc.chaos

import cn.hz.ddbm.pc.chaos.actions.TestFsmAction
import cn.hz.ddbm.pc.chaos.actions.TestSagaAction
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit4.SpringRunner

import javax.annotation.Resource

@SpringBootTest
@RunWith(SpringRunner)
@Import(TestConfig.class)
class AopAspectTest {

    @Resource
    TestFsmAction fsmAction;
    @Resource
    TestSagaAction sagaAction;

    @Before
    void setUp() {
    }

    @Test
    void testSagaExe() {
        sagaAction.execute(null)
    }

}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme