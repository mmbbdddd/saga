package cn.hz.ddbm.pc.lock;

import cn.hz.ddbm.pc.core.support.Locker;
import org.redisson.Redisson;
import org.redisson.client.RedisException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.TimeUnit;

public class ReidsLocker implements Locker {
    @Autowired
    Redisson redisson;

    @Override
    public void tryLock(String key, Integer timeout) throws Exception {
        if (!redisson.getLock(key).tryLock(timeout, TimeUnit.MILLISECONDS)) {
            throw new RedisException();
        }
    }

    @Override
    public void releaseLock(String key) throws Exception {
        redisson.getLock(key).unlock();
    }
}
