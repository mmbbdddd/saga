package cn.hz.ddbm.pc.fsm;

public enum IdCardState {
    init,
    presend,
    auditing,
    su,
    fail,
    no_such_order,
    lost_date,


}