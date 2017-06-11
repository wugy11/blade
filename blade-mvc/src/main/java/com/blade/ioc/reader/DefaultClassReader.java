package com.blade.ioc.reader;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * 根据classpath加载类
 */
public class DefaultClassReader implements ClassReader {

    @Override
    public Set<Class<?>> getClass(String packageName) {
        return new ClassReaderTemplate() {
            @Override
            public boolean checkAddClass(Class<?> clazz) {
                return true;
            }
        }.getClass(packageName);
    }

    @Override
    public Set<Class<?>> getClass(String packageName, Class<?> parent) {
        return new ClassReaderTemplate() {
            @Override
            public boolean checkAddClass(Class<?> clazz) {
                return parent.isAssignableFrom(clazz) && !parent.equals(clazz);
            }
        }.getClass(packageName);
    }

    @Override
    public Set<Class<?>> getClassByAnnotation(Class<? extends Annotation> annotation) {
        return getClassByAnnotation("", annotation);
    }

    @Override
    public Set<Class<?>> getClassByAnnotation(String packageName, Class<? extends Annotation> annotation) {
        return new ClassReaderTemplate() {
            @Override
            public boolean checkAddClass(Class<?> clazz) {
                return clazz.isAnnotationPresent(annotation);
            }
        }.getClass(packageName);
    }
}