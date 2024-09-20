package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.config.Coast;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

@Data
public class FlowContext<S extends State> {
    public BaseFlow<S>         flow;
    public S                   state;
    public String              event;
    public Object              action;
    public Boolean             fluent  = true;
    public String              errorMessage;
    public Map<String, Object> session = new HashMap<>();

    public Integer executeTimes() {
        return ((AtomicInteger) session.computeIfAbsent(Coast.STATISTICS.EXECUTE_TIMES, (Function<String, AtomicInteger>) s -> new AtomicInteger(0))).get();
    }
}
