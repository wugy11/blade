package com.blade.kit;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by wugy on 2017/6/6.
 */
abstract public class CollectionKit {

	public static <K, V> Map<K, V> newHashMap() {
		return new HashMap<>();
	}

	public static <K, V> Map<K, V> newHashMap(int size) {
		return new HashMap<>(size);
	}

	public static <K, V> ConcurrentHashMap<K, V> newConcurrentMap() {
		return new ConcurrentHashMap<>();
	}

	public static <K, V> ConcurrentHashMap<K, V> newConcurrentMap(int size) {
		return new ConcurrentHashMap<>(size);
	}

	public static <E> List<E> newArrayList() {
		return new ArrayList<>();
	}

	public static <E> List<E> newArrayList(int size) {
		return new ArrayList<>(size);
	}

	public static <E> Set<E> newHashSet() {
		return new HashSet<>();
	}

	public static boolean isEmpty(Collection<?> collection) {
		return null == collection || collection.isEmpty();
	}

	public static boolean isEmpty(Map<?, ?> map) {
		return null == map || map.isEmpty();
	}

	public static boolean isEmpty(Object[] arr) {
		return null == arr || arr.length == 0;
	}
}
