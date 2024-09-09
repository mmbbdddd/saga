package cn.hz.ddbm.pc.newcore.exception;

public class IdempotentException extends Exception {
    private Exception raw;

    public IdempotentException(Exception e) {
        super(e.getMessage(),e);
        this.raw = e;
    }

    public IdempotentException(String message) {
        super(message);
    }

    public IdempotentException(String message, Exception e) {
        super(message, e);
    }
}
