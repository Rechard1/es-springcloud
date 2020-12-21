package com.jwell56.security.cloud.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内网IP是以下面几个段的IP.用户可以自己设置.常用的内网IP地址:
 * <p>
 * 10.0.0.0~10.255.255.255
 * <p>
 * 172.16.0.0~172.31.255.255
 * <p>
 * 192.168.0.0~192.168.255.255
 */
public class IPUtil {

    public static boolean isInnerIP(String ip) {
        //String reg = "(10|172|192)\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})\\.([0-1][0-9]{0,2}|[2][0-5]{0,2}|[3-9][0-9]{0,1})";//正则表达式=。 =、懒得做文字处理了、
        String reg="^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})|(193\\.1\\.\\d{1,3}\\.\\d{1,3})$";
        Pattern p = Pattern.compile(reg);
        Matcher matcher = p.matcher(ip);
        return matcher.find();
    }

    public static boolean internalIp(byte[] addr) {
        final byte b0 = addr[0];
        final byte b1 = addr[1];
        //10.x.x.x/8
        final byte SECTION_1 = 0x0A;
        //172.16.x.x/12
        final byte SECTION_2 = (byte) 0xAC;
        final byte SECTION_3 = (byte) 0x10;
        final byte SECTION_4 = (byte) 0x1F;
        //192.168.x.x/16
        final byte SECTION_5 = (byte) 0xC0;
        final byte SECTION_6 = (byte) 0xA8;
        switch (b0) {
            case SECTION_1:
                return true;
            case SECTION_2:
                if (b1 >= SECTION_3 && b1 <= SECTION_4) {
                    return true;
                }
            case SECTION_5:
                switch (b1) {
                    case SECTION_6:
                        return true;
                }
            default:
                return false;

        }
    }

    /**
     * 校验IP是否合法
     *
     * @param text
     * @return
     */
    public static boolean ipCheck(String text) {

        if (!StringUtils.isEmpty(text)) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\."
                    + "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (text.matches(regex)) {
                return true;
            } else {
                return false;
            }
        }
        return false;

    }

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
    
    private static List<Long> getIps(String ip){
    	List<Long> ips = new ArrayList<Long>();
    	if (ip != null && !StringUtils.isEmpty(ip)) {
    		ip = ip.trim();
            String[] ipArr = ip.split("\\.");
            Long[] ipArrs = new Long[4];
            // 先找到IP地址字符串中.的位置
            // 将每个.之间的字符串转换成整型
            ipArrs[0] = Long.parseLong(ipArr[0]);
            ipArrs[1] = Long.parseLong(ipArr[1]);
            ipArrs[2] = Long.parseLong(ipArr[2]);
            ipArrs[3] = Long.parseLong(ipArr[3]);
            ips = Arrays.asList(ipArrs);
        }
    	return ips;
    }
    
    public static String nmapIps(String ip1, String ip2) {
    	StringBuilder sb = new StringBuilder();
    	List<Long> ip1List = getIps(ip1);
    	List<Long> ip2List = getIps(ip2);
    	if(!ip1List.get(0).equals(ip2List.get(0))) {
    		sb.append(ip1 + "-255");
    		sb.append(",");
    		sb.append(ip2 + "-255");
    	}else {
    		if(!ip1List.get(1).equals(ip2List.get(1))) {
    			if(ip1List.get(1) < ip2List.get(1)) {
    				sb.append(ip1List.get(0) + "." + ip1List.get(1) + "-" + ip2List.get(1) + ".");
    				if(ip1List.get(2) < ip2List.get(2)) {
    					sb.append(ip1List.get(2) + "-" + ip2List.get(2) + ".");
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}else {
    					sb.append(ip2List.get(2) + "-" + ip1List.get(2) + ".");
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}
    			}else {
    				sb.append(ip1List.get(0) + "." + ip2List.get(1) + "-" + ip1List.get(1) + ".");
    				if(ip1List.get(2) < ip2List.get(2)) {
    					sb.append(ip1List.get(2) + "-" + ip2List.get(2) + ".");
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}else {
    					sb.append(ip2List.get(2) + "-" + ip1List.get(2) + ".");
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}
    			}
    		}else {
    			if(!ip1List.get(2).equals(ip2List.get(2))) {
    				if(ip1List.get(2) < ip2List.get(2)) {
    					sb.append(ip1List.get(0) + "." + ip1List.get(1) + "." + ip1List.get(2) + "-" + ip2List.get(2) + ".");
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}else {
    					sb.append(ip1List.get(0) + "." + ip1List.get(1) + "." + ip2List.get(2) + "-" + ip1List.get(2) + ".");
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}
    			}else {
    				if(!ip1List.get(3).equals(ip2List.get(3))) {
    					if(ip1List.get(3) < ip2List.get(3)) {
    						sb.append(ip1List.get(0) + "." + ip1List.get(1) + "." + ip2List.get(2) + "." + ip1List.get(3) + "-" + ip2List.get(3));
    					}else {
    						sb.append(ip1List.get(0) + "." + ip1List.get(1) + "." + ip2List.get(2) + "." + ip2List.get(3) + "-" + ip1List.get(3));
    					}
    				}else {
    					sb.append(ip1);
    				}
    			}
    		}
    	}
    	return sb.toString();
    }

}