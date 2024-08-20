package cn.hz.ddbm.pc.core.utils;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.lang.func.Func0;

import java.util.List;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class RandomUitl {
    public static <S> S random(List<S> list) {
        int size   = list.size() * 10 - 1;
        int random = Double.valueOf(Math.random() * size / 10).intValue();
        return list.get(random);
    }

    static Cache<String, WeightRandom<?>> weightRandomCache = CacheUtil.newLRUCache(1000);

    public static <T> T selectByWeight(String key, Set<Pair<T, Double>> sets) {
        return (T) weightRandomCache.get(key, (Func0<WeightRandom<?>>) () -> new WeightRandom<>(sets)).random();
    }


//    public static void main(String[] args) {
//        Set<Pair<String, Double>> sets = Sets.set(
//                Pair.of("1", 0.1),
//                Pair.of("2", 0.2),
//                Pair.of("3", 0.3),
//                Pair.of("4", 0.4)
//        );
//        WeightRandom<String> weightRandom = new WeightRandom<>(sets);
//        for (int i = 0; i < 100; i++) {
//            System.out.println(weightRandom.random());
//        }
//    }

    public static class WeightRandom<K> {
        private TreeMap<Double, K> weightMap = new TreeMap<Double, K>();

        public WeightRandom(Set<Pair<K, Double>> list) {
            for (Pair<K, Double> pair : list) {
                double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey().doubleValue();//统一转为double
                this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());//权重累加
            }
        }

        public K random() {
            double               randomWeight = this.weightMap.lastKey() * Math.random();
            SortedMap<Double, K> tailMap      = this.weightMap.tailMap(randomWeight, false);
            return this.weightMap.get(tailMap.firstKey());
        }

    }
}
