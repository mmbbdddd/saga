package cn.hz.ddbm.pc.factory.json

import cn.hutool.json.JSONUtil
import cn.hz.ddbm.pc.core.Fsm
import cn.hz.ddbm.pc.core.Profile
import org.junit.Test

class JsonResourceTest {
    JsonResource jsonResource = new JsonResource()

    @Test
    void testResolve() {
        jsonResource.content = "{}"
        Fsm result = jsonResource.resolve()
        assert result == new Fsm(null, null, "init", ["ends"] as Set<String>, ["nodes"] as Set<String>, new Profile())
    }

    @Test
    void json() {
        println(JSONUtil.toJsonStr(new Fsm("", "", "init", ["su", "fail"] as Set, [] as Set, null)))
    }
}

//Generated with love by TestMe :) Please raise issues & feature requests at: https://weirddev.com/forum#!/testme