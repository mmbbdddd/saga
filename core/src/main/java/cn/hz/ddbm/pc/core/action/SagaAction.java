package cn.hz.ddbm.pc.core.action;

public interface SagaAction<S extends Enum<S>> extends CommandAction<S>, QueryAction<S> {


}