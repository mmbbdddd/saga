package cn.hz.ddbm.pc.newcore.infra;

import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.LockException;

/**
 * 锁接口
 */
public interface Locker {
    Coast.LockType code();

    void tryLock(String key, Integer timeout) throws LockException;

    void releaseLock(String key) throws LockException;
}
