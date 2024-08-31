package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
import cn.hz.ddbm.pc.newcore.exception.PauseException;
import cn.hz.ddbm.pc.newcore.exception.TransitionNotFoundException;

import java.util.concurrent.atomic.AtomicInteger;

public interface FlowProcessor<C extends FlowContext> {


    /**
     * 节点执行
     *
     * @param ctx
     * @throws FlowEndException     流程结束
     * @throws Exception            可连续重试
     * @throws InterruptedException 可调度重试
     * @throws PauseException       暂停，需人工
     */
    void workerProcess(C ctx) throws FlowEndException, InterruptedException, PauseException, TransitionNotFoundException;

    /**
     * 流程连续执行，直至异常
     *
     * @param ctx
     * @throws FlowEndException
     * @throws InterruptedException
     * @throws PauseException
     */
    default void flowProcess(C ctx) throws FlowEndException, InterruptedException, PauseException {
        //判断流程是否结束
        AtomicInteger loopTimes = new AtomicInteger(0);
        while (true) {
            try {
                workerProcess(ctx);
            } catch (InterruptedException e) {
                throw e;
            } catch (PauseException e) {
                e.printStackTrace();
                throw e;
            } catch (FlowEndException e) {
                e.printStackTrace();
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                if (loopTimes.incrementAndGet() > ctx.getProfile().getMaxLoopErrorTimes()) {
                    //todo
                    return;
                }
                ;
            }
        }
        //
    }
}
