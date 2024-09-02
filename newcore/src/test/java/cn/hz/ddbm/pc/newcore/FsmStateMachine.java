package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.exception.FlowEndException;
import lombok.Data;

public class FsmStateMachine<S> {
    Integer     index;
    S           state;
    FsmPipeline pipeline;
    FsmAction   action;

    public FsmStateMachine(Integer index, S state, FsmPipeline pipeline) {
        this.index    = index;
        this.state    = state;
        this.pipeline = pipeline;
        this.action   = new FsmAction();
    }

    public void onEvent(FsmContext ctx) throws FlowEndException {

        switch (ctx.subState) {
            case task:
                try {
                    action.doIt();
                    ctx.updateState(state, SubState.failover);
                } catch (Exception e) {
                    ctx.updateState(state, SubState.failover);
                }
                break;
            case failover:
                try {
                    if (action.queryIt()) {
                        ctx.updateState(getNext().state, SubState.task);
                    } else {
                        if (retryOver()) {
                            ctx.updateState(getPre().state, SubState.task);
                        } else {
                            ctx.updateState(state, SubState.task);
                        }
                    }
                } catch (FlowEndException e) {
                    throw e;
                } catch (Exception e) {
                    ctx.updateState(state, SubState.failover);
                }
                break;
        }
        pipeline.doIt(ctx);
    }

    private FsmStateMachine<S> getPre() throws FlowEndException {
        if (index == 0) {
            throw  new FlowEndException();
        }
        return pipeline.pipelines.get(index - 1);
    }

    private FsmStateMachine<S> getNext() throws FlowEndException {
        if (index == pipeline.pipelines.size() - 1) {
            throw  new FlowEndException();
        }
        FsmStateMachine<S> next = pipeline.pipelines.get(index + 1);
        return next;
    }

    @Data
    static class FsmContext<S> {
        S        state;
        SubState subState;


        public void updateState(S state, SubState subState) {
            System.out.println(String.format("%s/%s>>>%s/%s", this.state, this.subState, state, subState));
            this.state    = state;
            this.subState = subState;
        }


    }

    private boolean retryOver() {
        return true;
    }

    class FsmAction {
        void doIt() {

        }

        Boolean queryIt() {
            return true;
        }
    }

    enum SubState {
        task,
        failover,
        retry,
    }

}
