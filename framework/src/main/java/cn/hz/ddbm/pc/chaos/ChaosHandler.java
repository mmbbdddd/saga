package cn.hz.ddbm.pc.chaos;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.text.csv.CsvReader;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hz.ddbm.pc.common.lang.Triple;
import cn.hz.ddbm.pc.newcore.fsm.FsmCommandAction;
import cn.hz.ddbm.pc.newcore.fsm.FsmRouterAction;
import cn.hz.ddbm.pc.newcore.infra.Locker;
import cn.hz.ddbm.pc.newcore.infra.SessionManager;
import cn.hz.ddbm.pc.newcore.infra.StatusManager;
import cn.hz.ddbm.pc.newcore.saga.SagaAction;
import cn.hz.ddbm.pc.newcore.utils.RandomUitl;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class ChaosHandler {
    static String ChaosRuleFile = "chaos.csv";
    Map<Pair<String, String>, Set<Pair<Rule, Double>>> chaosRuleMap;
    Map<Pair<String, String>, Set<Pair<Rule, Double>>> resultMap;

    @PostConstruct
    public void initRules() {
        BufferedReader reader    = ResourceUtil.getUtf8Reader("chaos.csv");
        CsvReader      csvReader = CsvUtil.getReader();
        List<Rule>     rules     = csvReader.read(reader, Rule.class);
        Map<Pair<String, String>, List<Triple<String, String, Rule>>> t1 = rules.stream()
                .filter(r -> r.getAction().equals("exception"))
                .map(r -> Triple.of(r.getType(), r.getMethod(), r))
                .collect(Collectors.groupingBy(t -> Pair.of(t.getLeft(), t.getMiddle())));
        Map<Pair<String, String>, List<Triple<String, String, Rule>>> t2 = rules.stream()
                .filter(r -> r.getAction().equals("result"))
                .map(r -> Triple.of(r.getType(), r.getMethod(), r))
                .collect(Collectors.groupingBy(t -> Pair.of(t.getLeft(), t.getMiddle())));
        this.chaosRuleMap = new HashMap<>();
        this.resultMap    = new HashMap<>();
        t1.forEach((pair, triple) -> {
            this.chaosRuleMap.put(pair, triple.stream()
                    .map(t -> Pair.of(t.getRight(), t.getRight().toWeight()))
                    .collect(Collectors.toSet()));
            //插入正常执行概率
            this.chaosRuleMap.get(pair).add(Pair.of(Rule.DEFAULT, 1.0));
        });
        t2.forEach((pair, triple) -> {
            this.resultMap.put(pair, triple.stream()
                    .map(t -> Pair.of(t.getRight(), t.getRight().toWeight()))
                    .collect(Collectors.toSet()));
        });

        int i = 0;

    }



    public void handle(ChaosTargetType chaosTargetType, Object proxy, Method method, Object[] args) throws Throwable {
        Class clz = getTargetClass(chaosTargetType);
        Set<Pair<Rule,Double>> rules = chaosRuleMap.get(Pair.of(clz.getSimpleName(),method.getName()));
        if (null != rules) {
            String classSimpleName = clz.getSimpleName();
            String key             = classSimpleName + "." + method.getName();
            Rule   rule            = RandomUitl.selectByWeight(key, rules);
            if (rule.isException()) {
                rule.raiseException();
            }
        }
    }

    private Class getTargetClass(ChaosTargetType type) {
        Class clz =null;
        switch (type){
            case status:
                return StatusManager.class;
            case session:
                return SessionManager.class;
            case lock:
                return Locker.class;
            case fsmAction:
                return FsmCommandAction.class;
            case fsmRouterAction:
                return FsmRouterAction.class;
            case sagaAction:
                return SagaAction.class;
            default:
                return FsmCommandAction.class;
        }
    }

    public Object generateResult(ChaosTargetType type, Object proxy, Method method, Object[] args) {
        Class clz = getTargetClass(type);
        String                  key         = clz.getSimpleName() + "." + method.getName();
        Set<Pair<Rule, Double>> sagaResultRules = resultMap.get(Pair.of(clz.getSimpleName(), method.getName()));
        if(null != sagaResultRules) {
            Rule rule = RandomUitl.selectByWeight(key, sagaResultRules);
            return rule.getValue();
        }else{
            return null;
        }
    }




}
