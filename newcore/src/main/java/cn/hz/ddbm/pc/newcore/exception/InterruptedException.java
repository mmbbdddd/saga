package cn.hz.ddbm.pc.newcore.exception;

public class InterruptedException extends Exception {
    Exception raw;

    public InterruptedException(String msg) {
        super(msg);
    }

    public InterruptedException(IdempotentException e) {
        this.raw = e;
    }
}
