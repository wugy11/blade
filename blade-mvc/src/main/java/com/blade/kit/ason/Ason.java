package com.blade.kit.ason;

import com.blade.kit.ClassKit;
import com.blade.kit.json.JSONArray;
import com.blade.kit.json.JSONException;
import com.blade.kit.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import static com.blade.kit.ason.Util.*;

/**
 * @author Aidan Follestad (afollestad)
 */
@SuppressWarnings({ "unchecked" })
public class Ason {

	private JSONObject json;
	private AsonSerializer serializer;
	private boolean loadedMyFields;

	public Ason(JSONObject stock) {
		this.json = stock;
		this.serializer = AsonSerializer.get();
	}

	public Ason() {
		this(new JSONObject());
	}

	public Ason(Map<String, Object> map) {
		this();
		for (String key : map.keySet()) {
			Object value = map.get(key);
			put(key, value);
		}
	}

	public Ason(String json) {
		if (json == null) {
			this.json = new JSONObject();
			return;
		}
		try {
			this.json = new JSONObject(json);
		} catch (JSONException e) {
			throw new InvalidJsonException(json, e);
		}
	}

	public static Ason serialize(Object object) {
		return AsonSerializer.get().serialize(object);
	}

	public static <T> AsonArray<T> serializeArray(Object object) {
		return (AsonArray<T>) AsonSerializer.get().serializeArray(object);
	}

	public static <T> AsonArray<T> serializeList(List<T> object) {
		return (AsonArray<T>) AsonSerializer.get().serializeList(object);
	}

	public static <T> T deserialize(String json, Class<T> cls) {
		if (isJsonArray(json)) {
			AsonArray<?> ason = new AsonArray<Object>(json);
			return AsonSerializer.get().deserializeArray(ason, cls);
		} else {
			Ason ason = new Ason(json);
			return AsonSerializer.get().deserialize(ason, cls);
		}
	}

	public static <T> T deserialize(Ason json, Class<T> cls) {
		return AsonSerializer.get().deserialize(json, cls);
	}

	public static <T> T deserialize(AsonArray<?> json, Class<T> cls) {
		return AsonSerializer.get().deserializeArray(json, cls);
	}

	public static <T> List<T> deserializeList(String json, Class<T> cls) {
		AsonArray<?> array = new AsonArray<Object>(json);
		return AsonSerializer.get().deserializeList(array, cls);
	}

	public static <T> List<T> deserializeList(AsonArray<?> json, Class<T> cls) {
		return AsonSerializer.get().deserializeList(json, cls);
	}

	private Ason putInternal(JSONArray intoArray, JSONObject intoObject, String key, Object value) {
		invalidateLoadedFields();
		if (value == null || JSONObject.NULL.equals(value) || JSONObject.NULL == value) {
			json.put(key, JSONObject.NULL);
			return this;
		} else if (ClassKit.isPrimitive(value) || value instanceof JSONObject || value instanceof JSONArray) {
			if (value instanceof Byte) {
				value = ((Byte) value).intValue();
			} else if (value instanceof Character) {
				value = value.toString();
			}
			if (intoArray != null) {
				intoArray.put(value);
			} else {
				json.put(key, value);
			}
		} else if (value instanceof Ason) {
			putInternal(intoArray, intoObject, key, ((Ason) value).toStockJson());
		} else if (value instanceof AsonArray) {
			putInternal(intoArray, intoObject, key, ((AsonArray<?>) value).toStockJson());
		} else if (value.getClass().isArray()) {
			putInternal(intoArray, intoObject, key, serializer.serializeArray(value));
		} else if (isList(value.getClass())) {
			putInternal(intoArray, intoObject, key, serializer.serializeList((List<?>) value));
		} else {
			putInternal(intoArray, intoObject, key, serializer.serialize(value));
		}
		return this;
	}

	public Ason putNull(String key) {
		return put(key, JSONObject.NULL);
	}

	public Ason put(String key, Object... values) {
		Object insertObject;
		if (values == null || values.length == 1) {
			insertObject = values != null ? values[0] : JSONObject.NULL;
		} else {
			JSONArray newArray = new JSONArray();
			for (Object value : values) {
				putInternal(newArray, null, null, value);
			}
			insertObject = newArray;
		}
		if (key.contains(".")) {
			final String[] splitKey = splitPath(key);
			Object target = followPath(json, key, splitKey, true);
			if (target instanceof JSONArray) {
				JSONArray arrayTarget = (JSONArray) target;
				String indexKey = splitKey[splitKey.length - 1].substring(1);
				int insertIndex = Integer.parseInt(indexKey);
				if (insertIndex > arrayTarget.length() - 1) {
					arrayTarget.put(insertObject);
				} else {
					arrayTarget.put(insertIndex, insertObject);
				}
			} else {
				// noinspection ConstantConditions
				((JSONObject) target).put(splitKey[splitKey.length - 1], insertObject);
			}
		} else {
			putInternal(null, null, key, insertObject);
		}
		return this;
	}

	public Ason remove(String key) {
		String[] splitKey = splitPath(key);
		if (splitKey.length == 1) {
			json.remove(key);
		} else {
			Object followed = followPath(json, key, splitKey, false);
			if (followed == null) {
				return this;
			}
			if (followed instanceof JSONArray) {
				JSONArray followedArray = (JSONArray) followed;
				int insertIndex = Integer.parseInt(splitKey[splitKey.length - 1].substring(1));
				followedArray.remove(insertIndex);
			} else {
				((JSONObject) followed).remove(splitKey[splitKey.length - 1]);
			}
		}
		return this;
	}

	public <T> T get(String key) {
		return get(key, (T) null);
	}

	public <T> T get(String key, T defaultValue) {
		Object result;
		if (key.contains(".")) {
			final String[] splitKey = splitPath(key);
			result = getPathValue(json, key, splitKey);
		} else {
			result = json.opt(key);
		}
		if (result == null || JSONObject.NULL.equals(result) || JSONObject.NULL == result) {
			return defaultValue;
		} else if (result instanceof JSONObject) {
			result = new Ason((JSONObject) result);
		} else if (result instanceof JSONArray) {
			result = new AsonArray<Object>((JSONArray) result);
		}
		if (result instanceof Float) {
			result = Float.valueOf((float) result).doubleValue();
		}
		return (T) result;
	}

	public boolean getBool(String key) {
		return getBool(key, false);
	}

	public boolean getBool(String key, boolean defaultValue) {
		return get(key, defaultValue);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		return get(key, defaultValue);
	}

	public short getShort(String key) {
		return getShort(key, (short) 0);
	}

	public short getShort(String key, short defaultValue) {
		return get(key, defaultValue);
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue) {
		return get(key, defaultValue);
	}

	public long getLong(String key) {
		return getLong(key, 0L);
	}

	public long getLong(String key, long defaultValue) {
		return get(key, defaultValue);
	}

	public float getFloat(String key) {
		return getFloat(key, 0f);
	}

	public float getFloat(String key, float defaultValue) {
		double value = getDouble(key, Float.valueOf(defaultValue).doubleValue());
		return Double.valueOf(value).floatValue();
	}

	public double getDouble(String key) {
		return getDouble(key, 0d);
	}

	public double getDouble(String key, double defaultValue) {
		return get(key, defaultValue);
	}

	public Character getChar(String key) {
		return getChar(key, null);
	}

	public Character getChar(String key, Character defaultValue) {
		String value = getString(key);
		if (value == null || value.length() == 0) {
			return defaultValue;
		}
		return value.charAt(0);
	}

	public byte getByte(String key) {
		return getByte(key, (byte) 0);
	}

	public byte getByte(String key, byte defaultValue) {
		return (byte) get(key, defaultValue);
	}

	public Ason getJsonObject(String key) {
		return get(key, (Ason) null);
	}

	public <T> AsonArray<T> getJsonArray(String key) {
		return (AsonArray<T>) get(key, (AsonArray<?>) null);
	}

	public <T> T get(String key, Class<T> cls) {
		return get(key, cls, null);
	}

	public <T> T get(String key, Class<T> cls, T defaultValue) {
		final Object value = get(key, (T) null);
		if (Util.isNull(value)) {
			return defaultValue;
		} else if (ClassKit.isPrimitive(cls) || cls == JSONObject.class || cls == JSONArray.class || cls == Ason.class
				|| cls == AsonArray.class) {
			return (T) value;
		} else if (cls.isArray()) {
			if (!(value instanceof AsonArray)) {
				throw new IllegalStateException("Expected a AsonArray to convert to " + cls.getName() + ", found "
						+ value.getClass().getName() + ".");
			}
			AsonArray<T> array = (AsonArray<T>) value;
			return AsonSerializer.get().deserializeArray(array, cls);
		} else if (isList(cls)) {
			throw new IllegalStateException(
					"Use getList(String, Class) instead of " + "get(String, Class) for deserializing arrays to Lists.");
		} else {
			if (!(value instanceof Ason)) {
				throw new IllegalStateException("Expected a Ason to convert to " + cls.getName() + ", found "
						+ value.getClass().getName() + ".");
			}
			Ason object = (Ason) value;
			return AsonSerializer.get().deserialize(object, cls);
		}
	}

	public <T> List<T> getList(String key, Class<T> itemCls) {
		final Object value = get(key, (T) null);
		if (Util.isNull(value)) {
			return null;
		}
		if (!(value instanceof AsonArray)) {
			throw new IllegalStateException(
					"Expected a AsonArray to convert to List, " + "found " + value.getClass().getName() + ".");
		}
		AsonArray<T> array = (AsonArray<T>) value;
		return AsonSerializer.get().deserializeList(array, itemCls);
	}

	public boolean has(String key) {
		return get(key) != null;
	}

	public boolean equal(String key, Object value) {
		Object actual = get(key);
		if (actual == null) {
			return value == null;
		}
		return actual.equals(value);
	}

	//
	////// SERIALIZATION
	//

	public boolean isNull(String key) {
		Object value = get(key);
		return Util.isNull(value);
	}

	@Override
	public int hashCode() {
		return json.hashCode();
	}

	public int size() {
		invalidateLoadedFields();
		return json.length();
	}

	//
	////// DESERIALIZATION
	//

	public JSONObject toStockJson() {
		return json;
	}

	private void invalidateLoadedFields() {
		if (loadedMyFields) {
			return;
		}
		loadedMyFields = true;
		Field[] fields = getClass().getDeclaredFields();
		for (Field f : fields) {
			if (Modifier.isPrivate(f.getModifiers()) || shouldIgnore(f)) {
				continue;
			}
			f.setAccessible(true);
			String name = fieldName(f);
			try {
				put(name, f.get(this));
			} catch (IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Ason && ((Ason) obj).json.toString().equals(json.toString());
	}

	@Override
	public String toString() {
		invalidateLoadedFields();
		return json.toString();
	}

	public String toString(int indentSpaces) {
		invalidateLoadedFields();
		try {
			return json.toString(indentSpaces);
		} catch (JSONException e) {
			throw new IllegalStateException(e);
		}
	}

	public <T> T deserialize(Class<T> cls) {
		return deserialize(this, cls);
	}
}
