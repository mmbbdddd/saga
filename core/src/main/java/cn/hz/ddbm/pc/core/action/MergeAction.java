package cn.hz.ddbm.pc.core.action;

import cn.hz.ddbm.pc.core.action.decorator.ParallelActionDecorator;

import java.util.List;

public interface MergeAction <S extends Enum<S>> extends Action<S>{
    public S mergeResult(Boolean allThrough, List<ActionResult> results);
    public  class ActionResult {
        Enum state;

        public static ActionResult of(Enum state) {
            return null;
        }

        public static ActionResult exception(Enum state, Exception e) {
            return null;
        }
    }
}
