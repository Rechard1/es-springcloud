package com.jwell56.security.cloud.common.util;

import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertUtil {
    public static List<Map<String, Object>> dataObjectToKeyValue(Object obj) {
        List<Map<String, Object>> eventInfo = new ArrayList<>();
        if (obj == null) {
            return eventInfo;
        }
        Class clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                String key = "";
                Object value = field.get(obj);
                if (value instanceof LocalDateTime) {
                    value = FormatUtil.DateTime((LocalDateTime) value);
                }
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                if (apiModelProperty != null) {
                    if (!apiModelProperty.notes().isEmpty()) {
                        key = apiModelProperty.notes();
                    } else if (!apiModelProperty.value().isEmpty()) {
                        key = apiModelProperty.value();
                    }
                }
                if (!key.isEmpty()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("key", key);
                    map.put("value", value);
                    eventInfo.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eventInfo;
    }
}
