package cn.hz.ddbm.pc.idcardapply;

public enum IdCardFSM {
    MaterialCollection("收集材料中"),
    RuleChecked("规则检查"),
    Accepted("已受理"),
    Pass("通过"),
    Reject("不通过"),
    RuleSyncing("规则同步"),
    ;

    String descr;

    IdCardFSM(String descr) {
        this.descr = descr;
    }
}
