package com.jwell56.security.cloud.service.asset.utils;

import org.apache.commons.lang.time.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class NssaDateUtils {
    private static String[] parsePatterns = {"yyyy-MM-dd","yyyy年MM月dd日",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};

    public static Date parseDate(String string) {
        if (string == null) {
            return null;
        }
        try {
            return DateUtils.parseDate(string, parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }
}
