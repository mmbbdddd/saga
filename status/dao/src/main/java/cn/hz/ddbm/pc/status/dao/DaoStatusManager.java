package cn.hz.ddbm.pc.status.dao;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
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
    public void setStatus(String flow, Serializable flowId, Pair<FlowStatus, ?> statusPair, Integer timeout, FsmContext<?, ?> ctx) throws IOException {
        flowDaoMap.get(flow).save(ctx.getData());
    }

    @Override
    public Pair<FlowStatus, ?> getStatus(String flow, Serializable flowId) throws IOException {
        FsmPayload payload = flowDaoMap.get(flow).get(flow);
        return Pair.of(payload.getStatus(), payload.getState());
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
