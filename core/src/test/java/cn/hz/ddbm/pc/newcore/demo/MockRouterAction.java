package cn.hz.ddbm.pc.newcore.demo;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.utils.ExpressionEngineUtils;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;

import java.util.*;
import java.util.stream.Collectors;

public class MockRouterAction<S extends Enum<S>> implements RouterAction<S> {

    List<Pair<String,S>> routers;

    public MockRouterAction(Pair<String,S>... routers) {
        this.routers = Arrays.asList(routers);
    }

    @Override
    public Object execute(FsmConext<S> ctx) {
        return null;
    }

    @Override
    public S route(Object result, FsmConext<S> ctx) {
        Set<Pair<S,Double>> results = routers.stream().map(r->Pair.of(r.getValue(),1.0)).collect(Collectors.toSet());
        return RandomUitl.selectByWeight(this.toString(),results);
    }
}
