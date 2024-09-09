package cn.hz.ddbm.pc.newcore.exception;

public class StatusException extends RuntimeException {

    private final Exception raw;

    public StatusException(Exception e) {
        super(e.getMessage(),e);
        this.raw = e;
    }
}
