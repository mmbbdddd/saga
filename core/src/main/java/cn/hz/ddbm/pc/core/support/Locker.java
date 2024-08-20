package cn.hz.ddbm.pc.core.support;

public interface Locker {
    void tryLock(String key, Integer timeout) throws Exception;

    void releaseLock(String key) throws Exception;
}
