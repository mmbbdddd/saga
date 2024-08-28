package cn.hz.ddbm.pc.example;

public enum PayState {
    init("初始化"),
    freezed("已冻结（local）"),
    sended("已发送（remote）"),
    payed("已支付（remote）"),
    freezed_failover("冻结容错中"),
    sended_failover("发送容错中"),
    payed_failover("扣款容错中"),

    freezed_rollback("冻结回滚"),
    freezed_rollback_failover("冻结回滚容错"),
    su("成功"),
    fail("失败"),
    error("异常"),
    manual("人工处理")
    ;

    private final String descr;

    PayState(String descr) {
        this.descr = descr;
    }

}