package cn.hz.ddbm.pc.idcardapply.actions;

import cn.hz.ddbm.pc.newcore.FlowContext;
import cn.hz.ddbm.pc.newcore.fsm.action.LocalFsmAction;

public class RuleCheckedAction implements LocalFsmAction {
    @Override
    public Object localFsm(FlowContext ctx) throws Exception {
        return null;
    }

    @Override
    public String code() {
        return "ruleCheckedAction";
    }
}
