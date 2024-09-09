package cn.hz.ddbm.pc.newcore.exception;

public class SessionException extends Exception {


    private final Exception raw;

    public SessionException(Exception e) {
        super(e.getMessage(),e);
        this.raw = e;
    }
}
