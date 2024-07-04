package com.caesar.util;


import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanUtils {

    public static Map<String, String> getFieldNamesMapping(Class<?> clazz) {
        Map<String, String> fieldsMap = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            fieldsMap.put(field.getName(),StringUtils.toDBNamed(field.getName()));
        }
        return fieldsMap;
    }


    public static <T> Map<String, String> toDBNamedMap(T obj) {
        Map<String, String> fieldsMap = new HashMap<>();
        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                if (value != null) {
                    fieldsMap.put(StringUtils.toDBNamed(field.getName()), value.toString());
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return fieldsMap;
    }

}
