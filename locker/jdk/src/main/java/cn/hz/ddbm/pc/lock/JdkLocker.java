package cn.hz.ddbm.pc.lock;

import cn.hz.ddbm.pc.core.support.Locker;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class JdkLocker implements Locker {
    Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();

    @Override
    public void tryLock(String key, Integer timeout) throws Exception {
        try {
            locks.computeIfAbsent(key, s -> new ReentrantLock()).tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void releaseLock(String key) throws Exception {
        try {
            locks.get(key).unlock();
            locks.remove(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
