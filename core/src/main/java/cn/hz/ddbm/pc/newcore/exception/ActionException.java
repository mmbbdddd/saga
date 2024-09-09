package cn.hz.ddbm.pc.newcore.exception;

import lombok.Getter;

public class ActionException extends Exception {
    @Getter
    Exception raw;

    public ActionException(Exception e) {
        super(e.getMessage(),e);
        this.raw = e;
    }

}
