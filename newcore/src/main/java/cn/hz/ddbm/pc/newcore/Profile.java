package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.plugins.SagaDigestPlugin;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Profile {
    String               namespace;
    Integer              maxLoopErrorTimes = 3;
    Integer              statusTimeout;
    Integer              lockTimeout;
    Coast.StatusType     status;
    Coast.SessionType    session;
    Coast.LockType       lock;
    Coast.StatisticsType statistics;
    Coast.ScheduleType   schedule;

    List<Plugin> plugins;

    public Profile() {
        this.plugins = new ArrayList<>();
//        this.plugins.add(new FsmDigestPlugin());
        this.plugins.add(new SagaDigestPlugin());
    }

}
