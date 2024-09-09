package cn.hz.ddbm.pc.newcore.exception;

public class RetryableException extends Exception {
    Throwable raw;

    public RetryableException(Throwable e) {
        this.raw = e;
    }
}
