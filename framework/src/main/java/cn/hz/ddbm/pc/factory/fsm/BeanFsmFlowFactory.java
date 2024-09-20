package cn.hz.ddbm.pc.factory.fsm;

import cn.hz.ddbm.pc.newcore.BaseFlow;
import cn.hz.ddbm.pc.newcore.factory.FsmFlowFactory;
import cn.hz.ddbm.pc.newcore.fsm.FsmFlow;
import cn.hz.ddbm.pc.newcore.log.Logs;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 定义的流程的定义方式
 * 有xml，json，buider等方式。
 */
public class BeanFsmFlowFactory implements FsmFlowFactory, ApplicationContextAware {
    private ApplicationContext ctx;

    @Override
    public Map<String, FsmFlow> getFlows() {
        List<FsmFlow> flows = ctx.getBeansOfType(FSM.class).entrySet().stream().map(t -> {
            try {
                return t.getValue().build();
            } catch (Exception e) {
                Logs.error.error("", e);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return flows.stream().collect(Collectors.toMap(
                BaseFlow::getName,
                t -> t
        ));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
