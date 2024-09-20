package cn.hz.ddbm.pc.newcore;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;
@Data
public class FlowContext<S extends State> {
    public String              uuid;
    public String              id;
    public BaseFlow<S>         flow;
    public S                   state;
    public String              event;
    public Object              action;
    public Boolean             fluent  = true;
    public String              errorMessage;
    public Map<String, Object> session = new HashMap<>();

    public Profile getProfile() {
        return flow.getProfile();
    }
}
