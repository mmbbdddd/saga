package cn.hz.ddbm.pc.chaos;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Aspect
public class AopAspect {
    @Resource
    ChaosHandler chaosHandler;

    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.execute(..))")
    public Object sagaExe(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction,target, method, args);
        return pjp.proceed();
    }
    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.executeQuery(..))")
    public Object sagaExeQuery(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction,target, method, args);
        return chaosHandler.generateResult(ChaosTargetType.sagaAction,target, method, args);
    }
    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.rollback(..))")
    public Object sagaRollback(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction,target, method, args);
        return pjp.proceed();
    }
    @Around(" execution(* cn.hz.ddbm.pc.newcore.saga.SagaAction.rollbackQuery(..))")
    public Object sagaRollbackQuery(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.sagaAction,target, method, args);
        return chaosHandler.generateResult(ChaosTargetType.sagaAction,target, method, args);
    }
    @Around(" execution(* cn.hz.ddbm.pc.newcore.fsm.FsmCommandAction.command(..))")
    public Object fsmCommand(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.fsmAction,target, method, args);
        return pjp.proceed();
    }
    @Around(" execution(* cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction.execute(..))")
    public Object fsmExecute(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.fsmRouterAction,target, method, args);
        return pjp.proceed();
    }
    @Around(" execution(* cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction.executeQuery(..))")
    public Object fsmExecuteQuery(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.fsmRouterAction,target, method, args);
        return chaosHandler.generateResult(ChaosTargetType.fsmRouterAction,target, method, args);
    }


    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.Locker.*(..))")
    public Object locker(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.lock,target, method, args);
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.SessionManager.*(..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.session,target, method, args);
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.StatusManager.*(..))")
    public Object status(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(ChaosTargetType.status,target, method, args);
        return pjp.proceed();
    }
}
