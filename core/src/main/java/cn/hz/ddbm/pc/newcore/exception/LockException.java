package cn.hz.ddbm.pc.newcore.exception;

public class LockException extends Exception {
    private final Exception raw;

    public LockException(Exception e) {
        super(e.getMessage(),e);
        this.raw = e;
    }
}
