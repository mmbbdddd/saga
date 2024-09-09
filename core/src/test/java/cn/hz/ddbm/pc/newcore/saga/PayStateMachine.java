package cn.hz.ddbm.pc.newcore.saga;

public enum PayStateMachine {
    init, freezed, sended, fail, pay_failover, su, send_rollback, freeze_rollback, init_rollback, send
}
