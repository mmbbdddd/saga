package cn.hz.ddbm.pc.core.exception;

public class LockException extends WrapedRuntimeException {

    public LockException(Throwable raw) {
        super(raw);
    }
}
