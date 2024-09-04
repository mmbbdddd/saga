package cn.hz.ddbm.pc.newcore.fsm;


import java.util.Map;

public class FsmRouter<S> {
    String         noRecordExpression;
    String         prcessingExpression;
    Map<String, S> stateExpression;

    public FsmRouter(String noRecordExpression,String prcessingExpression, Map<String, S> stateExpression) {
        this.noRecordExpression = noRecordExpression;
        this.prcessingExpression = prcessingExpression;
        this.stateExpression    = stateExpression;
    }
}
