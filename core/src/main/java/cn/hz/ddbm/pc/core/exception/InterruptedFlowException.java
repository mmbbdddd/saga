package cn.hz.ddbm.pc.core.exception;


/**
 * @Description 中断异常
 * 停止连续执行，直到下一次事件再次触发
 * 触发时机：
 * 1，IO错误
 * 2，网络抖动等
 * @Author wanglin
 * @Date 2024/8/7 23:38
 * @Version 1.0.0
 **/


public class InterruptedFlowException extends RuntimeException {

    public InterruptedFlowException() {

    }

    public InterruptedFlowException(Exception e) {
        super(e);
    }
}
