package cn.hz.ddbm.pc.saga;

public enum PayState {
    init,
    freezed,
    sended,
    sendSuccess,
    sendFail,
    su,
    fail, freeze_rollback, send_rollback, error,


}