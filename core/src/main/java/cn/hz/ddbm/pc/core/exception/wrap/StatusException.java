package cn.hz.ddbm.pc.core.exception.wrap;

import cn.hz.ddbm.pc.core.exception.WrapedException;

public class StatusException extends WrapedException {

    public StatusException(Throwable raw) {
        super(raw);
    }
}
