package cn.hz.ddbm.pc.factory.fsm;

import cn.hz.ddbm.pc.factory.ResourceLoader;
import cn.hz.ddbm.pc.newcore.FlowModel;
import cn.hz.ddbm.pc.newcore.factory.FsmFlowFactory;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.log.Logs;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定义的流程的定义方式
 * 有xml，json，buider等方式。
 */
public class BeanFsmFlowFactory  implements FsmFlowFactory {


    @Override
    public Map<String, FsmFlow> getFlows() {
        List<FsmFlow> flows = loadResources().stream().map(t -> {
            try {
                return t.resolve();
            } catch (Exception e) {
                Logs.error.error("", e);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return flows.stream().collect(Collectors.toMap(
                FlowModel::getName,
                t->t
        ));
    }

    private Collection<FsmResource> loadResources() {
        return null;
    }
}
