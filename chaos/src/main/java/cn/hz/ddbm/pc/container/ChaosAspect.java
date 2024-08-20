package cn.hz.ddbm.pc.container;

import cn.hz.ddbm.pc.container.chaos.ChaosHandler;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import javax.annotation.Resource;
import java.lang.reflect.Method;

@Aspect
public class ChaosAspect {
    @Resource
    ChaosHandler chaosHandler;

    @Around(" execution(* cn.hz.ddbm.pc.core.Action.execute(*))")
    public Object action(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(target, method, args);
        return pjp.proceed();
    }


    @Around("execution(* cn.hz.ddbm.pc.core.support.Locker.*(..))")
    public Object locker(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(target, method, args);
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.core.support.SessionManager.*(..))")
    public Object session(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(target, method, args);
        return pjp.proceed();
    }

    @Around("execution(* cn.hz.ddbm.pc.core.support.StatusManager.*(..))")
    public Object status(ProceedingJoinPoint pjp) throws Throwable {
        Object   target = pjp.getTarget();
        Method   method = ((MethodSignature) pjp.getSignature()).getMethod();
        Object[] args   = pjp.getArgs();
        chaosHandler.handle(target, method, args);
        return pjp.proceed();
    }
}
