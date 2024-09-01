package cn.hz.ddbm.pc.chaos;

import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmCommandAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;

import javax.annotation.Resource;
import java.io.Serializable;

public class ChaosAction implements SagaAction, FsmCommandAction, FsmRouterAction {

    @Override
    public String code() {
        return "chaosAction";
    }

    @Override
    public void command(FsmContext ctx) throws ActionException {

    }

    @Override
    public void execute(FsmContext ctx) throws ActionException {

    }

    @Override
    public Serializable executeQuery(FsmContext ctx) throws NoSuchRecordException, ActionException {
        return null;
    }


    @Override
    public void execute(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }

    @Override
    public void rollback(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return null;
    }
}
