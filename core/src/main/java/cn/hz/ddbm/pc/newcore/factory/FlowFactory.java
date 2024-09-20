package cn.hz.ddbm.pc.newcore.factory;

import cn.hz.ddbm.pc.newcore.BaseFlow;

import java.util.Map;

public interface FlowFactory<F extends BaseFlow> {

    Map<String, F> getFlows();
}
