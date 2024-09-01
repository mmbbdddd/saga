package cn.hz.ddbm.pc.core.utils;

import cn.hz.ddbm.pc.core.exception.WrapedException;

public class ExceptionUtils {
    public static Throwable unwrap(Throwable e) {
        if (e instanceof WrapedException) {
            return ((WrapedException) e).getRaw();
        }
        return e;
    }
}
