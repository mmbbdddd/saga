package cn.hz.ddbm.pc.newcore.exception;

public class InterruptedException extends Exception {
    Throwable raw;

    public InterruptedException(String msg) {
        super(msg);
    }


    public InterruptedException(Throwable e) {
        this.raw = e;
    }
}
