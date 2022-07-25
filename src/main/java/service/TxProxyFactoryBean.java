package service;

import beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import proxy.TransactionHandler;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {
    Object target;
    PlatformTransactionManager transactionManager;
    String pattern;
    Class<?> serviceInterface;
    public void setTarget(Object target) {
        this.target = target;
    }

    public Object getObject() {
        TransactionHandler txHandler = new TransactionHandler();
        txHandler.setTarget(target);
        txHandler.setTransactionManager(transactionManager);
        txHandler.setPattern(pattern);
        return Proxy.newProxyInstance(
                getClass().getClassLoader(), new Class[]{serviceInterface}, txHandler);
    }
}
