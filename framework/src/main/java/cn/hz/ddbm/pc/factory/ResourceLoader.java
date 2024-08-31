package cn.hz.ddbm.pc.factory;


import cn.hz.ddbm.pc.factory.fsm.FsmResource;

import java.util.List;

/**
 * 定义了流程配置的存储介质
 * 参见resource模块
 */
public interface ResourceLoader<R extends FsmResource> {
    List<R> loadResources();
}
