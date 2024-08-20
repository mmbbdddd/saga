package cn.hz.ddbm.pc.core.utils;


import cn.hutool.extra.spring.SpringUtil;
import cn.hz.ddbm.pc.core.ValueObject;
import cn.hz.ddbm.pc.core.support.Locker;
import cn.hz.ddbm.pc.core.support.SessionManager;
import cn.hz.ddbm.pc.core.support.StatisticsSupport;
import cn.hz.ddbm.pc.core.support.StatusManager;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class InfraUtils {
    static Map<SessionManager.Type, SessionManager> sessionManagerMap;
    static Map<StatusManager.Type, StatusManager>   statusManagerMap;

    public InfraUtils() {
        sessionManagerMap = SpringUtil.getBeansOfType(SessionManager.class).values().stream().collect(Collectors.toMap(
                SessionManager::code,
                t -> t
        ));
        statusManagerMap  = SpringUtil.getBeansOfType(StatusManager.class).values().stream().collect(Collectors.toMap(
                StatusManager::code,
                t -> t
        ));
    }

    public static SessionManager getSessionManager(SessionManager.Type code) {
        return sessionManagerMap.get(code);
    }

    public static StatusManager getStatusManager(StatusManager.Type code) {
        return statusManagerMap.get(code);
    }

    public static StatisticsSupport getMetricsTemplate() {
        return SpringUtil.getBean(StatisticsSupport.class);
    }

    public static ExecutorService getPluginExecutorService() {
        return Executors.newFixedThreadPool(3);
    }

    public static Locker getLocker() {
        return SpringUtil.getBean(Locker.class);
    }


    public static String getDomain() {
        return "";
    }


    public static Object getBean(String beanName) {
        return SpringUtil.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return SpringUtil.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return SpringUtil.getBean(name, clazz);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        return SpringUtil.getBeansOfType(type);
    }

    public static <T extends ValueObject> List<T> getByCodesOfType(List<String> codes, Class<T> type) {
        Map<String, T> beans = SpringUtil.getBeansOfType(type);
        return beans.values().stream().filter(t -> codes.contains(t.code())).collect(Collectors.toList());
    }

    public static <T extends ValueObject> T getByCodeOfType(String code, Class<T> type) {
        Map<String, T> beans = SpringUtil.getBeansOfType(type);
        return beans.values().stream().filter(t -> code.contains(t.code())).findFirst().get();
    }

}
