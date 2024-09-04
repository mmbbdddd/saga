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

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.Locker.*(..))")
    public Object locker(ProceedingJoinPoint pjp) throws Throwable {
        chaosHandler.handle();
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.SessionManager.*(..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        chaosHandler.handle();
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.newcore.infra.StatusManager.*(..))")
    public Object status(ProceedingJoinPoint pjp) throws Throwable {
        chaosHandler.handle();
        return pjp.proceed();
    }
}
