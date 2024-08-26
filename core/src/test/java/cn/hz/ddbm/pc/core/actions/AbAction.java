package cn.hz.ddbm.pc.core.actions;

import cn.hz.ddbm.pc.core.FsmContext;
import cn.hz.ddbm.pc.core.action.CommandAction;
import org.springframework.stereotype.Component;

@Component("ab")
public class AbAction implements CommandAction {
    @Override
    public String beanName() {
        return "ab";
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {

    }


}
