package com.jwell56.security.cloud.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.DecimalFormat;

/**
 * @author wsg
 * @since 2019/4/11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesUtil {
    private Integer timeType;
    private Integer countTotal;
    private Integer beforeTotal;
    private String desTime;
    private String desPeriod;
    private String desCrease;
    private long sub;
    private long per;
    private String perStr;

    public DesUtil(Integer timeType, Integer countTotal, Integer beforeTotal) {
        this.timeType = timeType;
        this.countTotal = countTotal;
        this.beforeTotal = beforeTotal;
    }
    public DesUtil invoke() {
        final String DES_INCREASE = "增加";
        final String DES_DECREASE = "减少";

        desTime = this.getTimeDes(timeType);
        desPeriod = this.getPeriodDes(timeType);

        desCrease = countTotal > beforeTotal ? DES_INCREASE : DES_DECREASE;
        sub = countTotal > beforeTotal ? countTotal - beforeTotal : beforeTotal - countTotal;

        DecimalFormat df = new DecimalFormat("#.00");
        if (beforeTotal != 0 && countTotal != 0) {
            if (countTotal > beforeTotal) {
                per = (countTotal - beforeTotal) * 100 / beforeTotal;
                double perd = ((double) countTotal - (double) beforeTotal) * 100 / (double) beforeTotal;
                perStr = df.format(perd);
            } else {
                per = (beforeTotal - countTotal) * 100 / beforeTotal;
                double perd = ((double) beforeTotal - (double) countTotal) * 100 / (double) beforeTotal;
                perStr = df.format(perd);
            }
        } else {
            per = 100;
            perStr = "100.00";
        }
        if (perStr.equals(".00")) {
            perStr = "0";
        }
        return this;
    }

    public static String getTimeDes(Integer timeType) {
        final String DES_TIME_TYPE_1D = "今日";
        final String DES_TIME_TYPE_7D = "最近一周内";
        final String DES_TIME_TYPE_30D = "最近一月内";
        final String DES_TIME_TYPE_CUSTOM = "近期";
        timeType=timeType==null?TimeUtil.TIME_TYPE_1D:timeType;
        switch (timeType) {
            case TimeUtil.TIME_TYPE_CUSTOM:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_30M:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_1H:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_12H:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_1D:
                return DES_TIME_TYPE_1D;
            case TimeUtil.TIME_TYPE_7D:
                return DES_TIME_TYPE_7D;
            case TimeUtil.TIME_TYPE_30D:
                return DES_TIME_TYPE_30D;
            case TimeUtil.TIME_TYPE_NATIONAL:
                return "国庆期间";
            default:
                return DES_TIME_TYPE_CUSTOM;
        }
    }

    private static String getPeriodDes(Integer timeType) {
        final String DES_TIME_TYPE_1D = "昨日";
        final String DES_TIME_TYPE_7D = "上周";
        final String DES_TIME_TYPE_30D = "上月";
        final String DES_TIME_TYPE_CUSTOM = "之前";
        timeType=timeType==null?TimeUtil.TIME_TYPE_1D:timeType;
        switch (timeType) {
            case TimeUtil.TIME_TYPE_CUSTOM:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_30M:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_1H:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_12H:
                return DES_TIME_TYPE_CUSTOM;
            case TimeUtil.TIME_TYPE_1D:
                return DES_TIME_TYPE_1D;
            case TimeUtil.TIME_TYPE_7D:
                return DES_TIME_TYPE_7D;
            case TimeUtil.TIME_TYPE_30D:
                return DES_TIME_TYPE_30D;
            default:
                return DES_TIME_TYPE_CUSTOM;
        }
    }
}
