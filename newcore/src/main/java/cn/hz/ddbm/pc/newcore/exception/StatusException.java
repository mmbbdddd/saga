package cn.hz.ddbm.pc.newcore.exception;

public class StatusException extends RuntimeException {

    private final Exception raw;

    public StatusException(Exception e) {
        this.raw = e;
    }
}
