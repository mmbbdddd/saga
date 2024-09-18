# 简要说明

一般而言，大部分系统代码量和精力分配符合2/8原则
    
    80%的CRUD代码，占用了20%的精力
    20%的核心代码，占用了80%的精力

这20%的一般是长、复杂、灵活多变的业务核心流程。

这类业务的矛盾

    1，多变和高可用
    2，长、多变和一致性、可靠性
    3，复杂、高可用的业务和频繁多变、快速响应的业务需求的矛盾


在技术上，其架构的最佳实践是
<HR><H3>
流程编排 

    1，将复杂业务拆分成N个标准积木块
    2，使用流程编排确保其灵活性
    3，使用saga事务确保其一致性

</H3><HR>
 



# 快速开始

1， 业务场景

身份证办理
![复杂、长、自由流程](复杂、长、自由流程.png)

2， 编排流程

```java

    public void transitions(Transitions<IdCardFSM> transitions) {
        transitions
                .state(IdCardFSM.MaterialCollection)
                .local(Coast.EVENT_DEFAULT, MaterialCollectionAction.class, new ToRouter<>(IdCardFSM.RuleChecked))
                .endState()
                .state(IdCardFSM.RuleChecked)
                .local(Coast.EVENT_DEFAULT, RuleCheckedAction.class, new Router<>(new HashMap<String, IdCardFSM>() {{
                    put("true", IdCardFSM.Accepted);
                    put("false", IdCardFSM.MaterialCollection);
                }}))
                .endState()
                .state(IdCardFSM.Accepted)
                .remote(Coast.EVENT_DEFAULT, SendBizAction.class, new Router<>(
                        new HashMap<String, IdCardFSM>() {{
                            put("true", IdCardFSM.Accepted);
                            put("false", IdCardFSM.MaterialCollection);
                        }}))
                .endState()
                .state(IdCardFSM.RuleSyncing)
                .remote(Coast.EVENT_DEFAULT, SendBizAction.class, new Router<>(
                        new HashMap<String, IdCardFSM>() {{
                            put("true", IdCardFSM.RuleChecked);
                            put("false", IdCardFSM.RuleSyncing);
                        }}))
                .endState()
        ;
    }
```

2，测试

```log
2024-09-18 21:27:59.002  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=MaterialCollection, right=task}.materialCollectionAction==>Triple{left=null, middle=RuleChecked, right=task}
2024-09-18 21:27:59.003  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleChecked, right=task}.ruleCheckedAction==>Triple{left=null, middle=Accepted, right=task}
2024-09-18 21:27:59.005  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=Accepted, right=failover}.sendBizAction==>Triple{left=null, middle=Accepted, right=failover}
2024-09-18 21:27:59.005  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=Accepted, right=failover}.sendBizAction==>Triple{left=null, middle=RuleSyncing, right=task}
2024-09-18 21:27:59.005  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleSyncing, right=failover}.sendBizAction==>Triple{left=null, middle=RuleSyncing, right=failover}
2024-09-18 21:27:59.005  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleSyncing, right=failover}.sendBizAction==>Triple{left=null, middle=RuleChecked, right=task}
2024-09-18 21:27:59.005  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleChecked, right=task}.ruleCheckedAction==>Triple{left=null, middle=Accepted, right=task}
2024-09-18 21:27:59.005  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=Accepted, right=failover}.sendBizAction==>Triple{left=null, middle=Accepted, right=failover}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=Accepted, right=failover}.sendBizAction==>Triple{left=null, middle=RuleSyncing, right=task}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleSyncing, right=failover}.sendBizAction==>Triple{left=null, middle=RuleSyncing, right=failover}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleSyncing, right=failover}.sendBizAction==>Triple{left=null, middle=RuleChecked, right=task}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleChecked, right=task}.ruleCheckedAction==>Triple{left=null, middle=Accepted, right=task}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=Accepted, right=failover}.sendBizAction==>Triple{left=null, middle=Accepted, right=failover}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=Accepted, right=failover}.sendBizAction==>Triple{left=null, middle=RuleSyncing, right=task}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleSyncing, right=failover}.sendBizAction==>Triple{left=null, middle=RuleSyncing, right=failover}
2024-09-18 21:27:59.006  INFO 18568 --- [pool-1-thread-1] digest                                   : idcard,0,Triple{left=null, middle=RuleSyncing, right=failover}.sendBizAction==>Triple{left=null, middle=RuleChecked, right=task}
2024-09-18 21:27:59.009 ERROR 18568 --- [pool-1-thread-1] error                                    : 

cn.hz.ddbm.pc.newcore.exception.InterruptedException: 节点Triple{left=null, middle=RuleChecked, right=task}执行次数超限制4>3
	at cn.hz.ddbm.pc.newcore.fsm.FsmProcessor.workerProcess(FsmProcessor.java:70) ~[classes/:na]
	at cn.hz.ddbm.pc.newcore.fsm.FsmProcessor.workerProcess(FsmProcessor.java:46) ~[classes/:na]
	at cn.hz.ddbm.pc.newcore.FlowProcessor.flowProcess(FlowProcessor.java:33) ~[classes/:na]
	at cn.hz.ddbm.pc.chaos.ChaosService.lambda$fsm$1(ChaosService.java:107) ~[classes/:na]
	at java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511) ~[na:1.8.0_422]
	at java.util.concurrent.FutureTask.run(FutureTask.java:266) ~[na:1.8.0_422]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149) ~[na:1.8.0_422]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624) ~[na:1.8.0_422]
	at java.lang.Thread.run(Thread.java:750) ~[na:1.8.0_422]

2024-09-18 21:27:59.010  INFO 18568 --- [           main] flow                                     : 混沌测试报告：\n
2024-09-18 21:27:59.010  INFO 18568 --- [           main] flow                                     : InterruptedException:节点Triple{left=null, middle=RuleChecked, right=task}执行次数超限制4>3,	1


```
 