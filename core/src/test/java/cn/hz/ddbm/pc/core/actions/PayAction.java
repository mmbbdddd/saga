package cn.hz.ddbm.pc.core.actions;

import cn.hz.ddbm.pc.core.Action;
import cn.hz.ddbm.pc.core.FsmContext;
import org.springframework.stereotype.Component;

@Component
public class PayAction implements Action {
    @Override
    public String beanName() {
        return "payAction";
    }


    @Override
    public void execute(FsmContext ctx) throws Exception {
    }


}
