package cn.hz.ddbm.pc.newcore.infra.proxy;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.LockException;
import cn.hz.ddbm.pc.newcore.infra.Locker;

public class LockProxy implements Locker {
    Locker locker;

    public LockProxy(Locker t) {
        this.locker = t;
    }

    @Override
    public Coast.LockType code() {
        return locker.code();
    }

    @Override
    public void tryLock(String key, Integer timeout) throws LockException {
        locker.tryLock(key, timeout);
    }

    @Override
    public void releaseLock(String key) throws LockException {
        try {
            locker.releaseLock(key);
        } catch (Exception e) {
            throw new LockException(e);
        }
    }

}
