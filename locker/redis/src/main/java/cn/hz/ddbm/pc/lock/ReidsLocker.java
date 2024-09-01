package cn.hz.ddbm.pc.lock;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.infra.Locker;
import org.redisson.Redisson;
import org.springframework.beans.factory.annotation.Autowired;

public class ReidsLocker implements Locker {
    @Autowired
    Redisson redisson;

    @Override
    public Coast.LockType code() {
        return null;
    }

    @Override
    public void tryLock(String key, Integer timeout) throws LockException {

    }

    @Override
    public void releaseLock(String key) throws LockException {

    }

//    @Override
//    public void tryLock(String key, Integer timeout) throws Exception {
//        if (!redisson.getLock(key).tryLock(timeout, TimeUnit.MILLISECONDS)) {
//            throw new RedisException();
//        }
//    }
//
//    @Override
//    public void releaseLock(String key) throws Exception {
//        redisson.getLock(key).unlock();
//    }
}
