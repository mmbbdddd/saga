package cn.hz.ddbm.pc.newcore.exception;

public class RetryableException extends Exception {
    Throwable raw;

    public RetryableException(Throwable e) {
        super(e.getMessage(),e);
        this.raw = e;
    }
}
