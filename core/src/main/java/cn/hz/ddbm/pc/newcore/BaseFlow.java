package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.utils.EnvUtils;
import lombok.Data;

import java.util.Objects;

@Data
public abstract class BaseFlow<S extends State> {
    protected String  name;
    protected Profile profile;

    public Integer getRetry(S state) {
        return getProfile().getStateAttrs(state).getRetry();
    }

    public void profile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {

        if (EnvUtils.isChaos()) {
            return profile == null ? Profile.chaosOf() : profile;
        } else {
            return profile == null ? Profile.of() : profile;
        }
    }

    /**
     * 未结束，可运行
     * @param ctx
     * @return
     */
    public abstract boolean keepRun(FlowContext<S> ctx);

    public abstract void execute(FlowContext<S> ctx) throws Exception;
}
