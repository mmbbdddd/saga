package cn.hz.ddbm.pc.newcore.utils;


import cn.hutool.core.util.ReflectUtil;
import cn.hz.ddbm.pc.newcore.Profile;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.*;

import java.io.IOException;

public class ExceptionUtils {
    public static Throwable unwrap(Throwable e) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        return e;
    }


    public static boolean isRetryable(Throwable e) {
        if (e instanceof ActionException) {
            return true;
        }
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isRetryException = false;
        isRetryException |= e instanceof IOException;
        isRetryException |= e instanceof ActionException;
        return isRetryException;
    }

    public static boolean isInterrupted(Throwable e) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isInterruptedException = false;
        isInterruptedException |= e instanceof LockException;
        return isInterruptedException;
    }

    public static boolean isPaused(Throwable e) {
        if (ReflectUtil.getFieldValue(e, "raw") != null && ReflectUtil.getFieldValue(e, "raw") instanceof Exception) {
            e = (Exception) ReflectUtil.getFieldValue(e, "raw");
        }
        Boolean isPausedException = false;
        isPausedException |= e instanceof IllegalArgumentException;
        return isPausedException;
    }

    public static boolean isStoped(Throwable e) {
        return e instanceof FlowEndException;
    }

    public static void wrap(Throwable e, Profile profile) throws FlowEndException, InterruptedException, PauseException, RetryableException {
//        throws IdempotentException, ActionException, LockException, FlowEndException, NoSuchRecordException
        ///////停止执行
        if (e instanceof FlowEndException) {
            throw (FlowEndException) e;
        }
        ///////暂停执行，下次事件重新触发
        ///////暂停执行，下次任务不触发
        if (e instanceof PauseException) {
            throw (PauseException) e;
        }
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        if (!profile.isRetryableException(e)) {
            throw new RetryableException(e);
        } else {
            throw new InterruptedException(e);
        }
    }
}
