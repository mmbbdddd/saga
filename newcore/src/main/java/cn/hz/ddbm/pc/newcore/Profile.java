package cn.hz.ddbm.pc.newcore;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.newcore.chaos.ChaosRule;
import cn.hz.ddbm.pc.newcore.config.Coast;
import lombok.Builder;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

@Setter
@Builder
public class Profile {
    String                namespace;
    Integer               maxLoopErrorTimes   = 3;
    Integer               statusTimeoutMicros = 3000;
    Integer               lockTimeoutMicros   = 3000;
    Coast.StatusType      status;
    Coast.SessionType     session;
    Coast.LockType        lock;
    Coast.StatisticsType  statistics;
    Coast.ScheduleType    schedule;
    List<Plugin>          plugins;
    Map<State, StateAttr> stateAttr;
    ChaosConfig           chaos;


    public static Profile of() {
        return new ProfileBuilder().namespace("default")
                .maxLoopErrorTimes(2)
                .statusTimeoutMicros(3000)
                .lockTimeoutMicros(3000)
                .status(Coast.StatusType.redis)
                .session(Coast.SessionType.redis)
                .lock(Coast.LockType.redis)
                .statistics(Coast.StatisticsType.redis)
                .schedule(Coast.ScheduleType.timer)
                .plugins(new ArrayList<>())
                .stateAttr(new HashMap<>())
                .chaos(new ChaosConfig())
                .build();
    }

    public static Profile devOf() {
        return new ProfileBuilder().namespace("default")
                .maxLoopErrorTimes(10)
                .statusTimeoutMicros(3000)
                .lockTimeoutMicros(3000)
                .status(Coast.StatusType.jvm)
                .session(Coast.SessionType.jvm)
                .lock(Coast.LockType.jvm)
                .statistics(Coast.StatisticsType.jvm)
                .schedule(Coast.ScheduleType.timer)
                .plugins(new ArrayList<>())
                .stateAttr(new HashMap<>())
                .chaos(new ChaosConfig())
                .build();
    }

    public StateAttr getStateAttrs(State state) {
        return stateAttr.getOrDefault(state, StateAttr.defaultOf());
    }

    public String getNamespace() {
        return namespace == null ? "app" : namespace;
    }

    public Integer getMaxLoopErrorTimes() {
        return maxLoopErrorTimes == null ? 3 : maxLoopErrorTimes;
    }

    public Integer getStatusTimeoutMicros() {
        return statusTimeoutMicros == null ? 3000 : statusTimeoutMicros;
    }

    public Integer getLockTimeoutMicros() {
        return lockTimeoutMicros == null ? 3000 : lockTimeoutMicros;
    }

    public Coast.StatusType getStatus() {
        return status == null ? Coast.StatusType.redis : status;
    }

    public Coast.SessionType getSession() {
        return session == null ? Coast.SessionType.redis : session;
    }

    public Coast.LockType getLock() {
        return lock == null ? Coast.LockType.redis : lock;
    }

    public Coast.StatisticsType getStatistics() {
        return statistics == null ? Coast.StatisticsType.redis : statistics;
    }

    public Coast.ScheduleType getSchedule() {
        return schedule == null ? Coast.ScheduleType.timer : schedule;
    }

    public List<Plugin> getPlugins() {
        return plugins == null ? new ArrayList<>() : plugins;
    }

    public Map<State, StateAttr> getStateAttr() {
        return stateAttr == null ? new HashMap<>() : stateAttr;
    }

    public ChaosConfig getChaos() {
        return chaos == null ? new ChaosConfig() : chaos;
    }

    @Setter
    public static class ChaosConfig {
        Set<Pair<Boolean, Double>>  sagaRouterRules;




        public Set<Pair<Boolean, Double>> getSagaRouterRules() {
            return sagaRouterRules == null ? ChaosRule.defaultSagaResults() : sagaRouterRules;
        }


    }
}
