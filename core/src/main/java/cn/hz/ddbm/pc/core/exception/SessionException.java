package cn.hz.ddbm.pc.core.exception;

import java.io.IOException;

public class SessionException extends Exception {
    public SessionException(IOException e) {
        super(e);
    }
}
