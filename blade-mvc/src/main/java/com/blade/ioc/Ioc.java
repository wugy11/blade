package com.blade.ioc;

import java.util.List;
import java.util.Set;

/**
 * IOC container, it provides an interface for registration and bean.
 */
public interface Ioc {

    void addBean(Object bean);

    <T> T addBean(Class<T> type);

    void addBean(String name, Object bean);

    void setBean(Class<?> type, Object proxyBean);

    Object getBean(String name);

    <T> T getBean(Class<T> type);

    List<BeanDefine> getBeanDefines();

    BeanDefine getBeanDefine(Class<?> type);

    List<Object> getBeans();

    Set<String> getBeanNames();

    void remove(Class<?> type);

    void remove(String beanName);

    void clearAll();

}