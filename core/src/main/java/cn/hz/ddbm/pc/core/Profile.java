package cn.hz.ddbm.pc.core;

import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.multi.RowKeyTable;
import cn.hutool.core.map.multi.Table;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class Profile<S extends Enum<S>> {
    private Integer                                retry         = Coasts.DEFAULT_RETRY;
    private Integer                                statusTimeout = Coasts.DEFAULT_STATUS_TIMEOUT;
    private Integer                                lockTimeout   = Coasts.DEFAULT_LOCK_TIMEOUT;
    private SessionManager.Type                    sessionManager;
    private StatusManager.Type                     statusManager;
    private Table<S, String, Set<Pair<S, Double>>> maybeResults;
    private Map<S, StepAttrs>                      nodeAttrs;
    private Map<String, ActionAttrs>               actionAttrs;


    public Profile(SessionManager.Type sessionManager, StatusManager.Type statusManager) {
        this.sessionManager = sessionManager == null ? SessionManager.Type.redis : sessionManager;
        this.statusManager  = statusManager == null ? StatusManager.Type.redis : statusManager;
        this.actionAttrs    = new HashMap<>();
        this.nodeAttrs      = new HashMap<>();
        this.maybeResults   = new RowKeyTable<>();
    }

    public static Profile defaultOf() {
        return new Profile(SessionManager.Type.redis, StatusManager.Type.redis);
    }

    public static Profile chaosOf() {
        return new Profile(SessionManager.Type.memory, StatusManager.Type.memory);
    }

    public static Profile devOf() {
        return new Profile(SessionManager.Type.memory, StatusManager.Type.memory);
    }

    public void setStepAttrs(S state, StepAttrs stepAttrs) {
        stepAttrs.profile = this;
        this.nodeAttrs.put(state, stepAttrs);
    }

    public StepAttrs getStepAttrsOrDefault(S state) {
        return this.nodeAttrs.getOrDefault(state, new StepAttrs(this));
    }


    public static class StepAttrs {
        Profile profile;
        Integer retry;

        public StepAttrs(Profile profile) {
            this.profile = profile;
        }

        public Integer getRetry() {
            if (null == retry) {
                return profile.getRetry();
            }
            return retry;
        }
    }

    @Data
    public static class ActionAttrs {
        Integer failover;

    }
}
