package com.blade.ioc;

import java.lang.reflect.Field;

/**
 * Bean Field Injector
 */
public class FieldInjector implements Injector {

    private Ioc ioc;
    private Field field;

    public FieldInjector(Ioc ioc, Field field) {
        this.ioc = ioc;
        this.field = field;
    }

    @Override
    public void injection(Object bean) {
        try {
            Class<?> fieldType = field.getType();
            Object value = ioc.getBean(fieldType);
            field.setAccessible(true);
            field.set(bean, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}