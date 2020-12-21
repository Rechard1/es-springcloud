package com.jwell56.security.cloud.service.apt.utils;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;

public class IPUtil {

    /**
     * IP转数字
     * ip：String转long
     * 将127.0.0.1形式的ip地址转换成十进制整数形式
     * 61.233.37.198->1038689734
     */
    public static long ipToLong(String strIp) {

        try {
            if (strIp != null && !StringUtils.isEmpty(strIp)) {

                String[] ipArr = strIp.split("\\.");
                long[] ip = new long[4];
                // 先找到IP地址字符串中.的位置
                // 将每个.之间的字符串转换成整型
                ip[0] = Long.parseLong(ipArr[0]);
                ip[1] = Long.parseLong(ipArr[1]);
                ip[2] = Long.parseLong(ipArr[2]);
                ip[3] = Long.parseLong(ipArr[3]);

                return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];

            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 数字反转成IP字符串
     * ip：String转long
     * 将十进制整数形式转换成127.0.0.1形式的ip地址
     * 1038689734->61.233.37.198
     */
    public static String longToIP(long longIp) {

        StringBuffer sb = new StringBuffer("");
        try {
            // 直接右移24位
            sb.append(String.valueOf((longIp >>> 24)));
            sb.append(".");
            // 将高8位置0，然后右移16位
            sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));
            sb.append(".");
            // 将高16位置0，然后右移8位
            sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));
            sb.append(".");
            // 将高24位置0
            sb.append(String.valueOf((longIp & 0x000000FF)));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}