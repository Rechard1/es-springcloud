package com.jwell56.security.cloud.common.util;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 本类说明：对象转Map
 */
public class ObjAnalysis {

    public static Map ConvertObjToMap(Object obj,Map<String, Object> reMap) {

        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Field f = obj.getClass().getDeclaredField(fields[i].getName());
                    f.setAccessible(true);
                    Object o = f.get(obj);

                    System.out.println(fields[i].getGenericType().toString());
                    // 如果类型是Date
                    if (fields[i].getGenericType().toString().equals(
                            "class java.time.LocalDateTime")) {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        reMap.put(fields[i].getName(),
                                formatter.format((LocalDateTime)o));
                    } else {
                        reMap.put(fields[i].getName(), o);
                    }


                } catch (NoSuchFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return reMap;
    }

}
