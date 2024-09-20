package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * @param <S>
 */
public class LocalFsmActionProxy<S extends Enum<S>>   {
    Class<? extends LocalFsmAction> actionClass;
    LocalFsmAction<S>               action;

    public LocalFsmActionProxy(Class<? extends LocalFsmAction> actionClass) {
        this.actionClass = actionClass;
    }


    public Object doLocal(FsmContext ctx) {
return null;
    }
}
