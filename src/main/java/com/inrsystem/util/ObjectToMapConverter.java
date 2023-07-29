package com.inrsystem.util;

import com.inrsystem.enums.ErrorEnum;
import com.inrsystem.exception.LocalRunTimeException;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ObjectToMapConverter {

    public static <T> Map<String, Object> toMap(T object) {
        Map<String, Object> map = new HashMap<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                throw new LocalRunTimeException(ErrorEnum.OBJECT_TO_MAP_ERROR);
            }
        }
        return map;
    }

}

