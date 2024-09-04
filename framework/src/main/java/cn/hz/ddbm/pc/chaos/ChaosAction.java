package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.lang.Pair;
import cn.hz.ddbm.pc.ProcesorService;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.config.Coast;
import cn.hz.ddbm.pc.newcore.exception.ActionException;
import cn.hz.ddbm.pc.newcore.exception.NoSuchRecordException;
import cn.hz.ddbm.pc.newcore.fsm.FsmAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;

import java.util.HashSet;
import java.util.Set;

public class ChaosAction implements SagaAction, FsmAction {
    @Override
    public String code() {
        return Coast.CHAOS_ACTION;
    }

    @Override
    public void execute(FsmContext ctx) throws ActionException {

    }

    @Override
    public Object executeQuery(FsmContext ctx) throws NoSuchRecordException, ActionException {
        return ProcesorService.chaosHandler().executeQuery(ctx);
    }


    @Override
    public void execute(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean executeQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return ProcesorService.chaosHandler().executeQuery(ctx);
    }

    @Override
    public void rollback(SagaContext<?> ctx) throws ActionException {

    }

    @Override
    public Boolean rollbackQuery(SagaContext<?> ctx) throws NoSuchRecordException, ActionException {
        return ProcesorService.chaosHandler().rollbackQuery(ctx);
    }
}
