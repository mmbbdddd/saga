package cn.hz.ddbm.pc.core;

import lombok.Getter;

@Getter
public class Node<S extends State> {
    S                  name;
    //    FlowStatus        type;
    Profile.StateAttrs attrs;


    //    public Node(S name, FlowStatus type, Profile<S> profile) {
    public Node(S name, Profile  profile) {
        this.name = name;
//        this.type  = type;
        this.attrs = profile.getStepAttrsOrDefault(name);
    }


    public Integer getRetry() {
        return attrs.getRetry();
    }


}
