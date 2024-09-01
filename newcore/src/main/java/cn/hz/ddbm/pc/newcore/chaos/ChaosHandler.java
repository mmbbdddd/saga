package cn.hz.ddbm.pc.newcore.chaos;


public interface ChaosHandler {

    void locker() throws RuntimeException;

    void session() throws RuntimeException;

    void statistics() throws RuntimeException;

    void status() throws RuntimeException;
}
