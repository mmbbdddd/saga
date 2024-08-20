package cn.hz.ddbm.pc.factory.json;

import cn.hutool.json.JSONUtil;
import cn.hz.ddbm.pc.core.Fsm;
import cn.hz.ddbm.pc.factory.Resource;

public class JsonResource extends Resource {

    String content;

    @Override
    public Fsm resolve() {
        return JSONUtil.toBean(content, Fsm.class);
    }
}
