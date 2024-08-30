package cn.hz.ddbm.pc.newcore.exception;

import java.io.IOException;

public class StatusException extends RuntimeException {

    private final Exception raw;

    public StatusException(Exception e) {
        this.raw = e;
    }
}
