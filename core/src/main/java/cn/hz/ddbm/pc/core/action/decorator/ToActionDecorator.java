package cn.hz.ddbm.pc.core.action.decorator;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.CommandAction;
import cn.hz.ddbm.pc.core.action.SagaAction;

public class ToActionDecorator implements CommandAction {
    String        beanName;
    CommandAction commandAction;

    public ToActionDecorator(String beanName,  CommandAction commandAction) {
        this.beanName      = beanName;
        this.commandAction = commandAction;
    }

    @Override
    public String beanName() {
        return beanName;
    }

    @Override
    public void execute(FsmContext ctx) throws Exception {
        commandAction.execute(ctx);
    }

}
