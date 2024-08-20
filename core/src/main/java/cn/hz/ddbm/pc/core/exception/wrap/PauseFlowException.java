package cn.hz.ddbm.pc.core.exception.wrap;


import cn.hz.ddbm.pc.core.exception.WrapedException;

/**
 * /**
 *
 * @Description 暂停异常
 * 停止连续执行，状态设置为Pause，下次事件不会再触发程序执行。
 * 触发时机：
 * 1，程序错误
 * 2，用户输入错误  IllegalEntityException
 * @Author wanglin
 * @Date 2024/8/7 23:42
 * @Version 1.0.0
 **/


public class PauseFlowException extends WrapedException {
    public PauseFlowException(Throwable raw) {
        super(raw);
    }
}
