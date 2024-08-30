package cn.hz.ddbm.pc.newcore.exception;

public class SessionException extends Exception {


    private final Exception raw;

    public SessionException(Exception e) {
        this.raw = e;
    }
}
