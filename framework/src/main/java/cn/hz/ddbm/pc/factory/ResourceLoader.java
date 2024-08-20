package cn.hz.ddbm.pc.factory;


import java.util.List;

/**
 * 定义了流程配置的存储介质
 * 参见resource模块
 */
public interface ResourceLoader<R extends Resource> {
    List<R> loadResources();
}
