package cn.hz.ddbm.pc.newcore.fsm;

import cn.hz.ddbm.pc.newcore.FlowStatus;
import cn.hz.ddbm.pc.newcore.Payload;
import lombok.Data;

import java.io.Serializable;


public interface FsmPayload<S extends Enum<S>> extends Payload<FsmState<S>> {


      Serializable getId() ;

      void setId(Serializable id);

      FlowStatus getStatus() ;

      void setStatus(FlowStatus status) ;

      S getFsmState() ;

      void setFsmState(S fsmState) ;

      FsmState.Offset getOffset() ;

      void setOffset(FsmState.Offset offset) ;

    default FsmState<S> getState() {
        return new FsmState<>(getStatus(), getFsmState(), getOffset());
    }

    default void setState(FsmState<S> state) {
        setStatus(state.getStatus());
        setFsmState(state.getState());
        setOffset(state.getOffset());
    }
}
