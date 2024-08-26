package cn.hz.ddbm.pc.core.action.type;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.CommandAction;

public class ToActionDecorator implements CommandAction {
    CommandAction commandAction;

    public ToActionDecorator(  CommandAction commandAction) {
        this.commandAction = commandAction;
    }

    @Override
    public String beanName() {
        return commandAction.beanName();
    }

    @Override
    public void execute(FsmContext ctx) throws Exception {
        commandAction.execute(ctx);
    }

}
