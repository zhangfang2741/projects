package org.finance.quant.digital.executor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public abstract class Executor implements ApplicationContextAware, InitializingBean {
    /**
     * ApplicationContext
     */
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() {
        this.execute();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    /**
     * 策略执行
     */
    public abstract void execute();
}
