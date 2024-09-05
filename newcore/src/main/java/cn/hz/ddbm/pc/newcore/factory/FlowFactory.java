package cn.hz.ddbm.pc.newcore.factory;

import cn.hz.ddbm.pc.newcore.FlowModel;

import java.util.Map;

public interface FlowFactory<F extends FlowModel> {

    Map<String, F> getFlows();
}
