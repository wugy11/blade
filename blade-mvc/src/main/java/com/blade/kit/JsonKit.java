package com.blade.kit;

import com.blade.kit.ason.Ason;

import java.util.AbstractList;
import java.util.List;

/**
 * @author biezhi 2017/6/2
 */
public final class JsonKit {

	private JsonKit() {
	}

	@SuppressWarnings("unchecked")
	public static String toString(Object object) {
		Class<?> cls = object.getClass();
		if (cls.isArray()) {
			return Ason.serializeArray(object).toString();
		}
		if (ClassKit.hasInterface(cls, List.class)) {
			return Ason.serializeList((List<? extends Object>) object).toString();
		}
		if (cls.getSuperclass().equals(AbstractList.class)) {
			return Ason.serializeList((List<? extends Object>) object).toString();
		}
		return Ason.serialize(object).toString();
	}

	public static String toString(Object object, int spaces) {
		return Ason.serialize(object).toString(spaces);
	}

	public static <T> T formJson(String json, Class<T> cls) {
		return Ason.deserialize(json, cls);
	}

}
