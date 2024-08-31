package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
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

    public Profile() {
        this.namespace           = "default";
        this.maxLoopErrorTimes   = 3;
        this.statusTimeoutMicros = 6000;
        this.lockTimeoutMicros   = 6000;
        this.status              = Coast.StatusType.redis;
        this.session             = Coast.SessionType.redis;
        this.lock                = Coast.LockType.redis;
        this.statistics          = Coast.StatisticsType.redis;
        this.schedule            = Coast.ScheduleType.timer;
    }

}
