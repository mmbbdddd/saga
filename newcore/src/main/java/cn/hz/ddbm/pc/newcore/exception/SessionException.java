package cn.hz.ddbm.pc.newcore.exception;

import java.io.IOException;

public class SessionException extends Exception {


    private final Exception raw;

    public SessionException(Exception e) {
        this.raw = e;
    }
}
