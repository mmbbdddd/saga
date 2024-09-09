package cn.hz.ddbm.pc.newcore.demo;

import cn.hz.ddbm.pc.newcore.utils.ExpressionEngineUtils;

import java.util.Map;

public class MockToAction<S extends Enum<S>> implements Action<S> {
    S target;

    public MockToAction(S target) {
        this.target = target;
    }

    @Override
    public S executeTo(FsmConext<S> ctx) {
        return target;
    }
}
