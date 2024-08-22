package cn.hz.ddbm.pc.core.utils

import org.junit.Test
import org.junit.Before
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.springframework.expression.ExpressionParser
import static org.mockito.Mockito.*

class ExpressionEngineUtilsTest {
    @Mock
    ExpressionParser parser
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