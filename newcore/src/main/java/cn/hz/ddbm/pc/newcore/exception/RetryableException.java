package cn.hz.ddbm.pc.newcore.exception;

import cn.hz.ddbm.pc.newcore.utils.ExceptionUtils;

public class RetryableException extends Exception {
    Throwable raw;
    public RetryableException(Throwable e) {
        this.raw = e;
    }
}
