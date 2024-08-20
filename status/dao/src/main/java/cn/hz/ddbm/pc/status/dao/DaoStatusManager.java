package cn.hz.ddbm.pc.status.dao;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.support.StatusManager;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.stream.Collectors;

public class DaoStatusManager implements StatusManager, InitializingBean, ApplicationContextAware {
    private Map<String, PayloadDao<?>> flowDaoMap;
    private ApplicationContext         ctx;

    @Override
    public Type code() {
        return Type.dao;
    }

    @Override
    public void setStatus(String flow, Serializable flowId, State<?> flowStatus, Integer timeout, FsmContext<?, ?> ctx) throws IOException {
        flowDaoMap.get(flow).save(ctx.getData());
    }


    @Override
    public State<?> getStatus(String flow, Serializable flowId) throws IOException {
        return flowDaoMap.get(flow).get(flow).getStatus();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        flowDaoMap = ctx.getBeansOfType(PayloadDao.class).values().stream().collect(Collectors.toMap(
                PayloadDao::flowName,
                t -> t
        ));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
