package cn.hz.ddbm.pc.newcore.exception;

public class TransitionNotFoundException extends InterruptedException {
    public TransitionNotFoundException(String msg) {
        super(msg);
    }
}
