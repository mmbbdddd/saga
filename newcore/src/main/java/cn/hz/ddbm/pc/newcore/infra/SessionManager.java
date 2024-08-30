package cn.hz.ddbm.pc.newcore.infra;


import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.SessionException;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

/**
 * @Description 会话接口
 * @Author wanglin
 * @Date 2024/8/7 21:29
 * @Version 1.0.0
 **/
public interface SessionManager {
    Coast.SessionType code();

    void set(String flowName, Serializable flowId, Map<String, Object> session) throws IOException;

    Map<String, Object> get(String flowName, Serializable flowId) throws IOException;

    default void flush(FlowContext ctx) throws SessionException {
        try {
            set(ctx.getFlow().getName(), ctx.getId(), ctx.getSession());
        } catch (IOException e) {
            throw new SessionException(e);
        }
    }


}
