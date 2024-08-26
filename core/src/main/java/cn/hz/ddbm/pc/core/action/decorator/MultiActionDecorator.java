package cn.hz.ddbm.pc.core.action.decorator;


import cn.hz.ddbm.pc.core.action.SagaAction;

/**
 * 支持aAction，bActioin，cAction.....组合成一个Action运行的写法。
 **/


public abstract class MultiActionDecorator implements SagaAction {
    String actionNames;
    Enum   failover;

    public MultiActionDecorator(String actionNames, Enum failover) {
        this.actionNames = actionNames;
        this.failover    = failover;
    }

    @Override
    public String beanName() {
        return actionNames;
    }

    @Override
    public Enum failover() {
        return failover;
    }
}
