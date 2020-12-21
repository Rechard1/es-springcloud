package com.jwell56.security.cloud.common.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author wsg
 * @since 2019/8/27
 */
public class FormatUtil {

    public static String pointToDown(String s) {
        return s.replace(".", "__");
    }

    public static String DownToPoint(String s) {
        return s.replace("__", ".");
    }


    public static String humpToUnderline(String s) {
        String result = s;

        for (char i = 'A'; i <= 'Z'; i++)
            result = result.replace(String.valueOf(i), "_" + (char) (i - 'A' + 'a'));

        if (result.startsWith("_"))
            result = result.substring(1);

        return result;
    }

    public static String DateTime(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        } else {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(localDateTime);
        }
    }

    public static LocalDateTime StringToDateTime(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        } else {
            return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    //2019/9/12 9:28:14
    public static LocalDateTime StringToDateTime2(String dateTime) {
        if (dateTime == null || dateTime.isEmpty()) {
            return null;
        } else {
            String[] dts = dateTime.split(" ");
            String[] date = dts[0].contains("/") ? dts[0].split("/") : dts[0].split("-");
            String[] time = dts[1].split(":");
            return LocalDateTime.of(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2]),
                    Integer.valueOf(time[0]), Integer.valueOf(time[1]), Integer.valueOf(time[2]));
        }
    }

    public static String QushiDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        } else {
            return DateTimeFormatter.ofPattern("MM/dd/yyyy").format(localDateTime);
        }
    }

    public static String QushiDate2(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return "";
        } else {
            return DateTimeFormatter.ofPattern("yyyy/MM/dd").format(localDateTime);
        }
    }
}
