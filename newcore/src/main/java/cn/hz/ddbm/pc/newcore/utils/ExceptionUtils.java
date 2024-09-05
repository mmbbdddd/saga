package cn.hz.ddbm.pc.newcore.utils;


import cn.hutool.core.util.ReflectUtil;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.LockException;

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
        return e instanceof FlowEndException ;
    }

}
