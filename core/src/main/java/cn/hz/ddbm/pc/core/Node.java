package cn.hz.ddbm.pc.core;

import cn.hz.ddbm.pc.core.enums.FlowStatus;
import lombok.Getter;

@Getter
public class Node<S extends Enum<S>> {
    S                 name;
    FlowStatus        type;
    Profile.StepAttrs attrs;


    public Node(S name, FlowStatus type, Profile<S> profile) {
        this.name  = name;
        this.type  = type;
        this.attrs = profile.getStepAttrsOrDefault(name);
    }


    public Integer getRetry() {
        return attrs.getRetry();
    }


}
