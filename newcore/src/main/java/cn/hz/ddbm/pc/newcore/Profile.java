package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
public class Profile {
    String               namespace;
    Integer              maxLoopErrorTimes   = 3;
    Integer              statusTimeoutMicros = 3000;
    Integer              lockTimeoutMicros   = 3000;
    Coast.StatusType     status;
    Coast.SessionType    session;
    Coast.LockType       lock;
    Coast.StatisticsType statistics;
    Coast.ScheduleType   schedule;
    List<Plugin>         plugins;


    public static Profile of() {
        return new ProfileBuilder()
                .namespace("default")
                .maxLoopErrorTimes(2)
                .statusTimeoutMicros(3000)
                .lockTimeoutMicros(3000)
                .status(Coast.StatusType.redis)
                .session(Coast.SessionType.redis)
                .lock(Coast.LockType.redis)
                .statistics(Coast.StatisticsType.redis)
                .schedule(Coast.ScheduleType.timer)
                .plugins(new ArrayList<>())
                .build();
    }

    public static Profile devOf() {
        return new ProfileBuilder()
                .namespace("default")
                .maxLoopErrorTimes(10)
                .statusTimeoutMicros(3000)
                .lockTimeoutMicros(3000)
                .status(Coast.StatusType.jvm)
                .session(Coast.SessionType.jvm)
                .lock(Coast.LockType.jvm)
                .statistics(Coast.StatisticsType.jvm)
                .schedule(Coast.ScheduleType.timer)
                .plugins(new ArrayList<>())
                .build();
    }
}
