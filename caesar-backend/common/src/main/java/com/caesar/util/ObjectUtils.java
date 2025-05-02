package com.caesar.util;

import org.reflections.Reflections;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * 对象通用抽象工具类
 */
public class ObjectUtils extends org.apache.commons.lang3.ObjectUtils{

	/**
	 * empty object
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(Object obj){
		return obj == null || isEmpty(obj.toString());
	}
	
	/**
	 * not empty object
	 *
	 * @param obj
	 * @return
	 */
	public static boolean isNotEmpty(Object obj){
		return !isEmpty(obj);
	}
	
	/**
	 * empty String
	 *
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(String str){
		return str == null || "".equals(str);
	}
	
	/**
	 * not empty String
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNotEmpty(String str){
		return !isEmpty(str);
	}

	/**
	 * empty collection
	 *
	 * @param collection
	 * @return
	 */
	public static boolean isEmpty(Collection<?> collection) {
		return (collection == null) || (collection.isEmpty());
	}
	
	/**
	 * not empty collection
	 *
	 * @param collection
	 * @return
	 */
	public static boolean isNotEmpty(Collection<?> collection) {
		return !isEmpty(collection);
	}
	
	/**
	 * empty Iterator
	 *
	 * @param iterator
	 * @return
	 */
	public static boolean isEmpty(Iterator<?> iterator) {
		return (iterator == null) || (iterator.hasNext());
	}
	
	/**
	 * not empty Iterator
	 *
	 * @param iterator
	 * @return
	 */
	public static boolean isNotEmpty(Iterator<?> iterator) {
		return !isEmpty(iterator);
	}

	/**
	 * empty map
	 *
	 * @param map
	 * @return
	 */
	public static boolean isEmpty(Map<?, ?> map) {
		return (map == null) || (map.isEmpty());
	}
	
	/**
	 * not empty map
	 *
	 * @param map
	 * @return
	 */
	public static boolean isNotEmpty(Map<?, ?> map) {
		return !isEmpty(map);
	}
	
	/**
	 * empty Object[]
	 *
	 * @param objs
	 * @return
	 */
	public static boolean isEmpty(Object[] objs) {
		return (objs == null) || (objs.length == 0);
	}
	
	/**
	 * not empty Object[]
	 *
	 * @param objs
	 * @return
	 */
	public static boolean isNotEmpty(Object[] objs) {
		return !isEmpty(objs);
	}


	/**
	 * 查找并返回给定类的所有子类
	 *
	 * @param clazz 给定的类
	 * @return 给定类的所有子类
	 */
	public static <T> Set<Class<? extends T>> findAllSubclasses(Class<T> clazz) {
		// 创建一个Reflections对象，扫描指定包和子包
		Reflections reflections = new Reflections("com.caesar");

		// 使用Reflections对象查找所有子类
		Set<Class<? extends T>> subclasses = reflections.getSubTypesOf(clazz);

		return subclasses;
	}



}
