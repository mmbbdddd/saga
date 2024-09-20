package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;

/**
 * @param <S>
 */
public class LocalFsmActionProxy<S extends Enum<S>> {
    Class<? extends LocalFsmAction> actionClass;
    LocalFsmAction<S>               action;

    public LocalFsmActionProxy(Class<? extends LocalFsmAction> actionClass) {
        this.actionClass = actionClass;
        this.action = ProcesorService.getAction(actionClass);
    }


    public Object doLocalFsm(FsmContext<S> ctx) throws ActionException {
        try {
            //开始事务
            Object result = action.doLocalFsm(ctx);
            SpringUtil.getBean(ProcesorService.class).metricsNode(ctx);
            //提交事务
            return result;
        } catch (Exception e) {
            //回滚事务。
            throw new ActionException(e);
        }
    }
}
