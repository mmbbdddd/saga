package cn.hz.ddbm.pc.example;

import cn.hz.ddbm.pc.core.FsmPayload;
import cn.hz.ddbm.pc.core.State;
import cn.hz.ddbm.pc.core.coast.Coasts;
import cn.hz.ddbm.pc.core.enums.FlowStatus;
import cn.hz.ddbm.pc.core.exception.SessionException;
import cn.hz.ddbm.pc.core.exception.wrap.StatusException;
import cn.hz.ddbm.pc.profile.ChaosPcService;
import cn.hz.ddbm.pc.profile.PcService;
import cn.hz.ddbm.pc.profile.StablePcService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Service
//@UseFsm(PayFsm.class)
public class PayService implements InitializingBean {
    static Map<Long, PayOrder> orders;
    @Autowired
    ChaosPcService pcService;

    //每个业务方法前后增加事件拦截。自动发出push事件。
//    @FsmWatch(ids = {"$order.id"})
    public void saveOrder(PayOrder order) {
        order.status = FlowStatus.INIT;
        order.state  = PayState.init;
        orders.put(order.orderId, order);
        try {
            pcService.execute("pay",order, Coasts.EVENT_DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PayOrder query(Long orderId) {
        return PayService.orders.get(orderId);
    }





    @Override
    public void afterPropertiesSet() throws Exception {
        PayService.orders = new HashMap<>();
    }

    public static class PayOrder implements FsmPayload<PayState> {
        Long       orderId;
        FlowStatus status;
        PayState   state;

        @Override
        public Serializable getId() {
            return orderId;
        }

        @Override
        public State<PayState> getStatus() {
            return new State<>(state, status);
        }

        @Override
        public void setStatus(State<PayState> state) {
            this.status = state.getStatus();
            this.state  = state.getState();
        }
    }
}
