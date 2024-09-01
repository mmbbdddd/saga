package cn.hz.ddbm.pc.factory.saga;

import cn.hz.ddbm.pc.newcore.factory.SagaFlowFactory;
import cn.hz.ddbm.pc.newcore.log.Logs;
import cn.hz.ddbm.pc.newcore.saga.SagaFlow;
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
public class BeanSagaFlowFactory implements SagaFlowFactory, ApplicationContextAware {
    private ApplicationContext ctx;

    @Override
    public Map<String, SagaFlow> getFlows() {
        List<SagaFlow> flows = ctx.getBeansOfType(SAGA.class).entrySet().stream().map(t -> {
            try {
                return t.getValue().build();
            } catch (Exception e) {
                Logs.error.error("", e);
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        return flows.stream().collect(Collectors.toMap(
                SagaFlow::getName,
                t -> t
        ));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
