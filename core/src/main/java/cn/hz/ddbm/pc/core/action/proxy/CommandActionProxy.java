package cn.hz.ddbm.pc.core.action.proxy;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.CommandAction;

public class CommandActionProxy implements CommandAction {
    CommandAction commandAction;

    public CommandActionProxy(CommandAction commandAction) {
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
