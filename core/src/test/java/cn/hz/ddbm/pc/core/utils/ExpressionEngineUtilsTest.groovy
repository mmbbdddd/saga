package cn.hz.ddbm.pc.core.utils

import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.expression.ExpressionParser

class ExpressionEngineUtilsTest {
    @Mock
    ExpressionParser      parser
    @InjectMocks
    ExpressionEngineUtils expressionEngineUtils

    @Before
    void setUp() {

    }

    @Test
    void testEval() {

        Integer result = ExpressionEngineUtils.eval("1 +1", ["context": "context"], Integer.class)
        assert result == 2
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme