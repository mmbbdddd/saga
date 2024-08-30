package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.plugins.DigestLogPlugin;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Profile {
    Integer           statusTimeout;
    Coast.StatusType  status;
    Coast.SessionType session;

    List<Plugin> plugins;

    public Profile() {
        this.plugins = new ArrayList<>();
        this.plugins.add(new DigestLogPlugin());
    }

}
