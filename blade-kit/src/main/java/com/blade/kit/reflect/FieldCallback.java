package com.blade.kit.reflect;

import java.lang.reflect.Field;

public interface FieldCallback {

	default void callBackField(Class<?> clazz) throws Exception {
		Class<?> tempClazz = clazz;
		do {
			Field[] fileds = tempClazz.getDeclaredFields();
			for (Field field : fileds) {
				ReflectKit.forceAccess(field);
				if (ReflectKit.isAvaliable(field))
					callBack(field);
			}
			tempClazz = tempClazz.getSuperclass();
		} while (null != tempClazz && !Object.class.equals(tempClazz));
	}

	void callBack(Field field) throws Exception;
}
