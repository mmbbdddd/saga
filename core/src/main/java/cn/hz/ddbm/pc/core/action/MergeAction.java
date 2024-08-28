package cn.hz.ddbm.pc.core.action;

import cn.hutool.core.lang.Pair;

import java.util.List;

public interface MergeAction<S extends Enum<S>> extends Action<S> {
    S mergeResult(Boolean allThrough, List<Pair<?, ?>> results);

}
