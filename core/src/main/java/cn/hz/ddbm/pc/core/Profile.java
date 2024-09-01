package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.support.ScheduleManger;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatusManager;
import lombok.Data;

import java.util.*;

@Data
public class Profile {
    private Integer                  retry         = Coasts.DEFAULT_RETRY;
    private Integer                  statusTimeout = Coasts.DEFAULT_STATUS_TIMEOUT;
    private Integer                  lockTimeout   = Coasts.DEFAULT_LOCK_TIMEOUT;
    private SessionManager.Type      sessionManager;
    private StatusManager.Type       statusManager;
    private Map<State, StateAttrs>   stateAttrs;
    private Map<String, ActionAttrs> actionAttrs;
    private List<Plugin>             plugins;


    public Profile(SessionManager.Type sessionManager, StatusManager.Type statusManager) {
        this.sessionManager = sessionManager == null ? SessionManager.Type.redis : sessionManager;
        this.statusManager  = statusManager == null ? StatusManager.Type.redis : statusManager;
        this.actionAttrs    = new HashMap<>();
        this.stateAttrs     = new HashMap<>();
        this.plugins        = new ArrayList<>();
    }

    public static Profile defaultOf(Map<State, FlowStatus.Type> stateTypes) {
        return new Profile(SessionManager.Type.redis, StatusManager.Type.redis);
    }

    public static Profile chaosOf() {
        return new Profile(SessionManager.Type.memory, StatusManager.Type.memory);
    }

    public static Profile devOf() {
        return new Profile(SessionManager.Type.memory, StatusManager.Type.memory);
    }

    public void setStepAttrs(State state, StateAttrs stepAttrs) {
        this.stateAttrs.put(state, stepAttrs);
    }

    public StateAttrs getStepAttrsOrDefault(State state) {
        return this.stateAttrs.getOrDefault(state, new StateAttrs());
    }

    public Integer getStateRetry(State state) {
        StateAttrs attrs = stateAttrs.get(state);
        if (null == attrs || attrs.getRetry() == null) {
            return retry;
        } else {
            return attrs.getRetry();
        }
    }

    @Data
    public static class StateAttrs {
        Integer retry;
    }

    @Data
    public static class ActionAttrs {
        Integer failover;

    }
}
