package cn.hz.ddbm.pc.core.exception.wrap;

import cn.hz.ddbm.pc.core.exception.WrapedException;

public class ActionException extends WrapedException {
    public ActionException(Exception e) {
        super(e);
    }


}
