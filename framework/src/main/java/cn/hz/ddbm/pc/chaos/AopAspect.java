package cn.hz.ddbm.pc.chaos;

import cn.hz.ddbm.pc.newcore.chaos.ChaosTargetType;
import cn.hz.ddbm.pc.newcore.fsm.FsmContext;
import cn.hz.ddbm.pc.newcore.saga.SagaContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 向组件注入混沌
 * 1，action
 * 2，锁
 * 3，会话
 * 4，状态
 * 5，事件溯源
 */
@Aspect
public class AopAspect {
    @Resource
    ChaosHandlerImpl chaosHandler;

    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.execute(..))")
    public Object sagaExe(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction, target, method, args);
        return pjp.proceed();
    }

    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.executeQuery(..))")
    public Object sagaExeQuery(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction, target, method, args);
        SagaContext ctx = (SagaContext) args[0];
        return chaosHandler.rollbackQuery(ctx);
    }

    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.rollback(..))")
    public Object sagaRollback(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction, target, method, args);
        return pjp.proceed();
    }

    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.rollbackQuery(..))")
    public Object sagaRollbackQuery(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction, target, method, args);
        SagaContext ctx = (SagaContext) args[0];
        return chaosHandler.rollbackQuery(ctx);
    }

    @Around(" execution(* cn.hz.ddbm.pc.newcore.fsm.FsmAction.execute(..))")
    public Object fsmExecute(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.fsmAction, target, method, args);
        return pjp.proceed();
    }

    @Around(" execution(* cn.hz.ddbm.pc.newcore.fsm.FsmAction.executeQuery(..))")
    public Object fsmExecuteQuery(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.fsmAction, target, method, args);
        FsmContext ctx = (FsmContext) args[0];
        return chaosHandler.executeQuery(ctx);
    }


    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.Locker.*(..))")
    public Object locker(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.lock, target, method, args);
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.SessionManager.*(..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.session, target, method, args);
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.StatusManager.*(..))")
    public Object status(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.status, target, method, args);
        return pjp.proceed();
    }
}
