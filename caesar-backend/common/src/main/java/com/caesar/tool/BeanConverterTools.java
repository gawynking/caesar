package com.caesar.tool;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class BeanConverterTools {

    // 转换单个对象
    public static <S, T> T convert(S source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }

        try {
            // 尝试使用无参构造器创建目标对象
            T target = createInstance(targetClass);
            if (target == null) {
                throw new RuntimeException("Failed to create instance of target class");
            }

            // 获取源对象的所有字段
            Field[] sourceFields = source.getClass().getDeclaredFields();
            for (Field sourceField : sourceFields) {
                sourceField.setAccessible(true);

                // 获取目标对象中与源字段同名的字段
                Field targetField;
                try {
                    targetField = targetClass.getDeclaredField(sourceField.getName());
                    targetField.setAccessible(true);

                    if(null == sourceField.get(source)) { // IllegalArgumentException
                       throw new IllegalArgumentException(sourceField.getName() + "值为null");
                    }

                    // 将源字段的值设置到目标字段中
                    targetField.set(target, sourceField.get(source));
                } catch (Exception e) {
                    // 目标对象中没有与源字段同名的字段，忽略
                }
            }

            return target;
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert bean", e);
        }
    }

    // 转换列表
    public static <T, U> List<U> convertList(List<T> sourceList, Class<U> targetClass) {
        List<U> targetList = new ArrayList<>();
        for (T source : sourceList) {
            U target = convert(source, targetClass);
            targetList.add(target);
        }
        return targetList;
    }

    // 创建目标对象实例
    private static <U> U createInstance(Class<U> targetClass) {
        try {
            // 尝试使用无参构造器
            Constructor<U> constructor = targetClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            // 无法使用无参构造器，尝试使用其它构造器
            Constructor<?>[] constructors = targetClass.getDeclaredConstructors();
            for (Constructor<?> cons : constructors) {
                cons.setAccessible(true);
                Class<?>[] paramTypes = cons.getParameterTypes();
                Object[] initargs = new Object[paramTypes.length];

                // 尝试用null初始化参数
                for (int i = 0; i < paramTypes.length; i++) {
                    initargs[i] = getDefault(paramTypes[i]);
                }

                try {
                    return (U) cons.newInstance(initargs);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    // 获取默认值
    private static Object getDefault(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            if (clazz == boolean.class) return false;
            if (clazz == byte.class) return (byte) 0;
            if (clazz == short.class) return (short) 0;
            if (clazz == int.class) return 0;
            if (clazz == long.class) return 0L;
            if (clazz == float.class) return 0.0f;
            if (clazz == double.class) return 0.0;
            if (clazz == char.class) return '\u0000';
        }
        return null;
    }

}