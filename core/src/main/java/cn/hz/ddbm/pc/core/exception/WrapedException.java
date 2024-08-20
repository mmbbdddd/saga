package cn.hz.ddbm.pc.core.exception;

import lombok.Getter;

public class WrapedException extends Exception {
    @Getter
    Throwable raw;

    public WrapedException(Throwable raw) {
        this.raw = raw;
    }
}
