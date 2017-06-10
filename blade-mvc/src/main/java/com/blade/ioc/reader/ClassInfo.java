package com.blade.ioc.reader;

public class ClassInfo {

    private String className;
    private Class<?> clazz;


    public ClassInfo(Class<?> clazz) {
        this.clazz = clazz;
        this.className = clazz.getName();
    }

   public String getClassName() {
        return className;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public Object newInstance() {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return clazz.toString();
    }
}