//package cn.hz.ddbm.pc.core.action.actiondsl;
//
//import cn.hutool.core.lang.Assert;
//import cn.hz.ddbm.pc.core.FsmContext;
//import cn.hz.ddbm.pc.core.Transition;
//import cn.hz.ddbm.pc.core.action.Action;
//import cn.hz.ddbm.pc.core.action.CommandAction;
//import cn.hz.ddbm.pc.core.action.QueryAction;
//import cn.hz.ddbm.pc.core.action.SagaAction;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class SerialAction extends MultiAction {
//    QueryAction queryAction;
//
//    List<SagaAction> sagaActions;
//
//
//    public SerialAction(Transition transition, List<Action> actions) {
//        super(transition, actions);
//        Assert.isTrue(queryActions.size() == 1, "ParallelActionDecorator.queryActions.size !=1");
//        this.queryAction = queryActions.get(0);
//        this.sagaActions = actions.stream().filter(t -> t instanceof SagaAction).map(SagaAction.class::cast).collect(Collectors.toList());
//    }
//    @Override
//    public Boolean executeWhen(FsmContext ctx) {
//        return sagaActions.stream().allMatch(sagaAction -> {
//            try {
//                return sagaAction.executeWhen(ctx);
//            } catch (Exception e) {
//                return false;
//            }
//        });
//    }
//    @Override
//    public void execute(FsmContext ctx) throws Exception {
//        for (CommandAction sCommandAction : commandActions) {
//            sCommandAction.execute(ctx);
//        }
//    }
//
//    @Override
//    public Enum queryState(FsmContext ctx) throws Exception {
//        return queryAction.queryState(ctx);
//    }
//
//
//
//}
