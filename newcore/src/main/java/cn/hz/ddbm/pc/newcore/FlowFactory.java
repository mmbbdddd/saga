package cn.hz.ddbm.pc.newcore;

import java.util.Map;

public interface FlowFactory<F extends FlowModel> {

    Map<String, F> getFlows();
}
