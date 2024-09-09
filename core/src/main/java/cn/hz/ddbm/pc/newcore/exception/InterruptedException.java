package cn.hz.ddbm.pc.newcore.exception;

import lombok.Getter;

public class InterruptedException extends Exception {
    @Getter
    Throwable raw;

    public InterruptedException(String msg) {
        super(msg);
    }


    public InterruptedException(Throwable e) {
        super(e.getMessage(),e);
        this.raw  =e;
    }
}
