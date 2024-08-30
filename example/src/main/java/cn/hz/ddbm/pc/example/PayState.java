package cn.hz.ddbm.pc.example;

public enum PayState {
    init("初始化"),
    freezed("已冻结（local）"),
    sended("已发送（remote）"),
    payed("已支付（remote）"),

    ;

    private final String descr;

    PayState(String descr) {
        this.descr = descr;
    }

}