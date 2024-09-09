package cn.hz.ddbm.pc.newcore.chaos;

import lombok.Data;

@Data
public class ChaosRule {
    Object value;


    public ChaosRule(Object value) {
        this.value  = value;
    }


    public boolean isException() {
        try {
            if (null != value && Throwable.class.isAssignableFrom((Class<?>) value)) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public void raiseException() throws Exception {
        Class     type = (Class) value;
        Exception e    = (Exception) type.newInstance();
        throw e;
    }


}
