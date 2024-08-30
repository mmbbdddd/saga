package cn.hz.ddbm.pc.newcore.infra;


import cn.hutool.extra.spring.SpringUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 基础设施类（隔离层，屏蔽对基础设施的依赖）
 */
public class InfraUtils {
    static ExecutorService                          es = null;
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
        es                = Executors.newFixedThreadPool(2);
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
        return es;
//        return getBean(Coast.PLUGIN_EXECUTOR_SERVICE, ExecutorService.class);
    }

    public static ExecutorService getActionExecutorService() {
        return es;
//        return getBean(Coast.ACTION_EXECUTOR_SERVICE, ExecutorService.class);
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
