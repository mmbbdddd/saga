//package cn.hz.ddbm.pc.factory.xml;
//
//
//import cn.hutool.json.JSONObject;
//import cn.hutool.json.JSONUtil;
//import cn.hz.ddbm.pc.factory.fsm.FsmResource;
//import cn.hz.ddbm.pc.factory.Resource;
//
//public class XmlResource extends Resource {
//
//    String content;
//
//    @Override
//    public FsmResource resolve() {
//        return JSONUtil.toBean(parseXmlToJson(content), FsmResource.class);
//    }
//
//    private JSONObject parseXmlToJson(String content) {
//        return JSONUtil.xmlToJson(content);
//    }
//}
