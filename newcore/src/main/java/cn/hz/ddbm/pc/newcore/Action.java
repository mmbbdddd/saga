package cn.hz.ddbm.pc.newcore;

import cn.hz.ddbm.pc.newcore.infra.ValueObject;
import org.springframework.beans.factory.BeanNameAware;

public interface Action extends ValueObject, BeanNameAware {
    @Override
    default void setCode(String code) {

    }

    @Override
    default void setBeanName(String beanName) {
        setCode(beanName);
    }
}
