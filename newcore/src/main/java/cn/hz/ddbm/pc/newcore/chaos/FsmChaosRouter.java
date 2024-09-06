package cn.hz.ddbm.pc.newcore.chaos;

import cn.hutool.core.lang.Pair;

import java.util.Map;
import java.util.Set;

public interface FsmChaosRouter <S extends Enum<S>>{
    Set<Pair<S,Double>> weights();
}
