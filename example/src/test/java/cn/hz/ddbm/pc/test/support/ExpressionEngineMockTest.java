package cn.hz.ddbm.pc.test.support;


import org.junit.Test;
import org.mvel2.MVEL;

import java.util.ArrayList;
import java.util.List;

public class ExpressionEngineMockTest {

    @Test
    public void ognl() {
        System.out.println(MVEL.eval("Math.random()"));
        System.out.println(MVEL.eval("Math.random()>0.7"));
//        try {
//            Ognl.add
//            System.out.println(Ognl.getValue("@java.lang.Math@random() > 0.7", new HashMap(){{
//                put("match",Math.abs());
//            }}));
//        } catch (OgnlException e) {
//            e.printStackTrace();
//        }
    }

//    @Test
//    public void fsmTables() {
//        List<FsmEvent> fsmTables = new ArrayList<>();
//        //本地方法
//        fsmTables.add(new FsmEvent());
////        本地直接方法
//        fsmTables.add(new FsmEvent());
//        //远程方法
//        fsmTables.add(new FsmEvent());
//    }
//
//    class FsmEvent {
//        String event;
//        Class  actionClass;
//        Router router;
//    }
//
//    interface Router<S> {
//        S router(Object result, Object ctx);
//    }
//
//    interface LocalRouter<S> extends Router<S> {
//    }
//    interface RemoteRouter<S> extends Router<S> {
//    }
}