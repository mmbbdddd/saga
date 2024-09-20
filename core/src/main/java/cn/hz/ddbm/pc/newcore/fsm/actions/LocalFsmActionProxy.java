package cn.hz.ddbm.pc.newcore.fsm.actions;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.ProcessorService;
import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.fsm.FsmState;

/**
 */
public class LocalFsmActionProxy {
    Class<? extends LocalFsmAction> actionClass;
    LocalFsmAction                  action;

    public LocalFsmActionProxy(Class<? extends LocalFsmAction> actionClass) {
        this.actionClass = actionClass;
        this.action      = ProcessorService.getAction(actionClass);
    }


    public <S extends Enum<S>> Object doLocalFsm(FlowContext<FsmState > ctx) throws Exception {
        try {
            //开始事务
            Object result = action.doLocalFsm(ctx);
            //提交事务
            return result;
        } catch (RuntimeException e) {
            //回滚事务。
            throw e;
        } catch (Exception e) {
            //回滚事务。
            throw e;
        } finally {
            SpringUtil.getBean(ProcessorService.class).metricsNode(ctx);
        }
    }
}
