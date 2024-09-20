package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowContext;
import lombok.Data;

@Data
public class FsmContext<S extends Enum<S>> extends FlowContext<FsmState<S>> {
}
