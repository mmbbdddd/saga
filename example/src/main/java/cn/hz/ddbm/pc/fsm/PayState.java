package cn.hz.ddbm.pc.fsm;

public enum PayState {
    init,
    freezed,

    sended,

    sendfailover,
    su,
    fail,
    error,


}