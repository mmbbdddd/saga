package cn.hz.ddbm.pc.factory;



public abstract class Resource<T> {
    public abstract T resolve() throws Exception;


}
