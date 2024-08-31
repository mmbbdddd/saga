package cn.hz.ddbm.pc.newcore.exception;

public class IdempotentException extends Exception {
    private final Exception raw;

    public IdempotentException(Exception e) {
        this.raw = e;
    }
}
