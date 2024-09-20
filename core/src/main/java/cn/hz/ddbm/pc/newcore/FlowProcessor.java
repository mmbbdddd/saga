//package cn.hz.ddbm.pc.newcore;
//
//import cn.hz.ddbm.pc.newcore.exception.InterruptedException;
//import cn.hz.ddbm.pc.newcore.exception.*;
//import cn.hz.ddbm.pc.newcore.log.Logs;
//
//public interface FlowProcessor  {
//
//
//    /**
//     * 节点执行
//     *
//     * @param ctx
//     * @throws FlowEndException     流程结束
//     * @throws Exception            可连续重试
//     * @throws InterruptedException 可调度重试
//     * @throws PauseException       暂停，需人工
//     */
//    void workerProcess(FlowContext ctx) throws FlowEndException, InterruptedException, PauseException, TransitionNotFoundException, RetryableException;
//
//    /**
//     * 流程连续执行，直至异常
//     *
//     * @param ctx
//     * @throws FlowEndException
//     * @throws InterruptedException
//     * @throws PauseException
//     */
//    default void flowProcess(FlowContext ctx) throws InterruptedException, PauseException {
//        //判断流程是否结束
//        while (true) {
//            try {
//                workerProcess(ctx);
//            } catch (FlowEndException e) {
//                Logs.flow.info("流程结束{},{}",ctx.getFlow().getName(),ctx.getId());
//                return;
//            } catch (PauseException e) {
//                //暂停，下次调度无法触发
//                throw e;
//            } catch (InterruptedException e) {
//                //暂停，等下次调度在执行
//                throw e;
//            } catch (RetryableException e) {
//                //继续执行
//            }
//        }
//        //
//    }
//}
