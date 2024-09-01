package cn.hz.ddbm.pc.newcore.infra;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 基础设施类（隔离层，屏蔽对基础设施的依赖）
 */
public class InfraUtils {
    static ExecutorService es = null;


    public InfraUtils() {
        es = Executors.newFixedThreadPool(2);
    }


    public static ExecutorService getPluginExecutorService() {
        return es;
//        return getBean(Coast.PLUGIN_EXECUTOR_SERVICE, ExecutorService.class);
    }

    public static ExecutorService getActionExecutorService() {
        return es;
//        return getBean(Coast.ACTION_EXECUTOR_SERVICE, ExecutorService.class);
    }


}
