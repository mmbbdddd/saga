package cn.hz.ddbm.pc.core.exception;

import lombok.Getter;

public class WrapedRuntimeException extends RuntimeException {
    @Getter
    Throwable raw;

    public WrapedRuntimeException(Throwable raw) {
        this.raw = raw;
    }
}
