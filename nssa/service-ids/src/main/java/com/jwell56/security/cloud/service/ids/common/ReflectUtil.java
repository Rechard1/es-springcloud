package com.jwell56.security.cloud.service.ids.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author wsg
 * @since 2020/11/19
 */
public class ReflectUtil {

    private static Logger logger = LoggerFactory.getLogger(ReflectUtil.class);


    public static Class<?> getGenericsFieldClazz(Class<?> clazz) {
        return getGenericsFieldClazz(clazz, null, 0);
    }

    public static Class<?> getGenericsFieldClazz(Class<?> clazz, String regex) {
        return getGenericsFieldClazz(clazz, regex, -1);
    }

    public static Class<?> getGenericsFieldClazz(Class<?> clazz, int index) {
        return getGenericsFieldClazz(clazz, null, index);
    }

    /**
     * 获取实体类中泛型字段的clazz
     *
     * @param clazz 实体类
     * @param regex 类名正则验证
     * @param index 字段序号
     * @return 泛型clazz
     */
    private static Class<?> getGenericsFieldClazz(Class<?> clazz, String regex, int index) {
        Class<?> rawType = null;
        try {
            Type[] actualTypeArguments = ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments();
            if (index >= 0) {
                rawType = (Class<?>) actualTypeArguments[index];
            } else if (regex != null && !regex.isEmpty()) {
                for (Type actualTypeArgument : actualTypeArguments) {
                    if (Pattern.matches(regex, actualTypeArgument.getTypeName())) {
                        rawType = (Class<?>) actualTypeArgument;
                    }
                }
            } else {
                rawType = (Class<?>) actualTypeArguments[0];
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return rawType;
    }

    /**
     * 获取包含父类的所有私有字段
     */
    public static Field[] getFieldsWithSupper(Class<?> clazz) {
        List<Field> fieldWithSupper = getFieldsWithSupper(clazz, null);
        Field[] fields = new Field[fieldWithSupper.size()];
        return fieldWithSupper.toArray(fields);
    }

    private static List<Field> getFieldsWithSupper(Class<?> clazz, List<Field> fieldList) {
        fieldList = fieldList == null ? new ArrayList<>() : fieldList;
        if (!clazz.getSuperclass().equals(Object.class)) {
            getFieldsWithSupper(clazz.getSuperclass(), fieldList);
        }
        fieldList.addAll(Arrays.asList(clazz.getDeclaredFields()));
//        fieldList.addAll(Arrays.asList(clazz.getFields()));
        return fieldList;
    }
}
