package com.jwell56.security.cloud.service.ids.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.common.util.FormatUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wsg
 * @since 2019/8/30
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Times {

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "Asia/Shanghai")
    @ApiModelProperty(value = "开始时间")
    private LocalDateTime startTime;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss" , timezone = "Asia/Shanghai")
    @ApiModelProperty(value = "结束时间")
    private LocalDateTime endTime;

    @ApiModelProperty(example = "6" , value = "时间范围类型,0:自定义,1:30分钟,2:1小时,3:12小时,4:1天,5:7天,6:30天" , allowableValues = "0,1,2,3,4,5,6")
    private Integer timeType;

    private static final boolean MODEL = true;//true.日周月以日期计算，false.日周月以24小时制计算

    public static final int TIME_TYPE_CUSTOM = 0;
    public static final int TIME_TYPE_30M = 1;
    public static final int TIME_TYPE_1H = 2;
    public static final int TIME_TYPE_12H = 3;
    public static final int TIME_TYPE_1D = 4;
    public static final int TIME_TYPE_7D = 5;
    public static final int TIME_TYPE_30D = 6;
    public static final int TIME_TYPE_YESTERDAY = 7;

    public static final int TIME_TYPE_NATIONAL = 101;//2019年国庆特殊参数
    public static final LocalDateTime nationalStart = FormatUtil.StringToDateTime("2019-10-01 00:00:00");
    public static final LocalDateTime nationalEnd = FormatUtil.StringToDateTime("2019-10-07 23:59:59");

    public Times(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.timeType = TIME_TYPE_CUSTOM;
    }

    private void init() {
        getStartTime();
    }

    private void initType() {
        timeType = timeType == null ? TIME_TYPE_30D : timeType;
    }

    public LocalDateTime getEndTime() {
        initType();
        switch (timeType) {
            case TIME_TYPE_CUSTOM:
                break;
            case TIME_TYPE_YESTERDAY:
                endTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusSeconds(1);
                break;
            case TIME_TYPE_NATIONAL:
                endTime = nationalEnd;
                break;
            default:
                endTime = LocalDateTime.now();
                break;
        }
        return endTime == null ? LocalDateTime.now() : endTime;
    }

    public LocalDateTime getStartTime() {
        initType();
        endTime = getEndTime();
        switch (timeType) {
            case TIME_TYPE_CUSTOM:
                break;
            case TIME_TYPE_30M:
                startTime = endTime.minusMinutes(30);
                break;
            case TIME_TYPE_1H:
                startTime = endTime.minusHours(1);
                break;
            case TIME_TYPE_12H:
                startTime = endTime.minusHours(12);
                break;
            case TIME_TYPE_1D:
                startTime = MODEL ? LocalDateTime.of(endTime.toLocalDate(), LocalTime.MIN) : endTime.minusDays(1);
                break;
            case TIME_TYPE_7D:
                startTime = MODEL ? LocalDateTime.of(endTime.toLocalDate().minusDays(6), LocalTime.MIN) : endTime.minusDays(7);
                break;
            case TIME_TYPE_30D:
                startTime = MODEL ? LocalDateTime.of(endTime.toLocalDate().minusDays(29), LocalTime.MIN) : endTime.minusDays(30);
                break;
            case TIME_TYPE_YESTERDAY:
                startTime = LocalDateTime.of(endTime.toLocalDate(), LocalTime.MIN);
                break;
            case TIME_TYPE_NATIONAL:
                startTime = nationalStart;
                break;
            default:
                startTime = endTime.minusDays(30);
                break;
        }
        return startTime;
    }

    public LocalDateTime hisEnd() {
        return his().getEndTime();
    }

    public LocalDateTime hisStart() {
        return his().getStartTime();
    }

    public Times his() {
        init();
        Times times;
        switch (timeType) {
            case TIME_TYPE_CUSTOM:
                long startTimeStamp = startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long endTimeStamp = endTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long subSeconds = endTimeStamp - startTimeStamp;
                times = new Times(startTime.minusSeconds(subSeconds), endTime.minusSeconds(subSeconds));
                break;
            case TIME_TYPE_30M:
                times = new Times(startTime.minusMinutes(30), endTime.minusMinutes(30));
                break;
            case TIME_TYPE_1H:
                times = new Times(startTime.minusHours(1), endTime.minusHours(1));
                break;
            case TIME_TYPE_12H:
                times = new Times(startTime.minusHours(12), endTime.minusHours(12));
                break;
            case TIME_TYPE_1D:
                times = new Times(startTime.minusDays(1), endTime.minusDays(1));
                break;
            case TIME_TYPE_YESTERDAY:
                times = new Times(startTime.minusDays(1), endTime.minusDays(1));
                break;
            case TIME_TYPE_7D:
                times = new Times(startTime.minusDays(7), endTime.minusDays(7));
                break;
            case TIME_TYPE_30D:
                times = new Times(startTime.minusDays(30), endTime.minusDays(30));
                break;
            case TIME_TYPE_NATIONAL:
                times = new Times(startTime.minusDays(7), endTime.minusDays(7));
                break;
            default:
                times = new Times(startTime.minusDays(30), endTime.minusDays(30));
        }
        return times;
    }

    public String des() {
        String dateDes = dateDes();
        if (!dateDes.isEmpty()) {
            return dateDes;
        } else {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return df.format(this.getStartTime()) + "至" + df.format(this.getEndTime());
        }
    }


    /**
     * 获取自然日、周、月的日期描述，非自然日、周、月返回空字符串
     */
    public String dateDes() {
        init();
        if (startTime.toLocalDate().isEqual(endTime.toLocalDate())) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            return df.format(startTime);
        }
        if (startTime.getDayOfWeek() == DayOfWeek.MONDAY &&
                endTime.getDayOfWeek() == DayOfWeek.SUNDAY &&
                endTime.minusDays(6).toLocalDate().equals(startTime.toLocalDate())) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月");
            return df.format(startTime) + "第" + (startTime.getDayOfMonth() / 7 + 1) + "周";
        }
        if (startTime.getDayOfMonth() == 1 &&
                endTime.plusDays(1).getDayOfMonth() == 1 &&
                startTime.getMonth() == endTime.getMonth() &&
                startTime.getYear() == endTime.getYear()) {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月");
            return df.format(startTime);
        }
        return "";
    }

    public List<Times> group() {
        return group(10);
    }

    public List<Times> group(Integer parts) {
        init();

        List<Times> timesList = new ArrayList<>();
        switch (timeType) {
            case TIME_TYPE_1D:
                for (int i = 0; i < 8; i++) {
                    if (startTime.plusHours(3 * (i + 1)).isAfter(endTime)) {
                        timesList.add(new Times(startTime.plusHours(3 * i), endTime));
                        break;
                    } else {
                        timesList.add(new Times(startTime.plusHours(3 * i), startTime.plusHours(3 * (i + 1))));
                    }
                }
                break;
            case TIME_TYPE_7D:
                for (int i = 0; i < 7; i++) {
                    if (startTime.plusDays(i + 1).isAfter(endTime)) {
                        timesList.add(new Times(startTime.plusDays(i), endTime));
                        break;
                    } else {
                        timesList.add(new Times(startTime.plusDays(i), startTime.plusDays(i + 1)));
                    }
                }
                break;
            case TIME_TYPE_30D:
                for (int i = 0; i < 10; i++) {
                    if (startTime.plusDays(3 * (i + 1)).isAfter(endTime)) {
                        timesList.add(new Times(startTime.plusDays(3 * i), endTime));
                        break;
                    } else {
                        timesList.add(new Times(startTime.plusDays(3 * i), startTime.plusDays(3 * (i + 1))));
                    }
                }
                break;
            default:
                long startTimeStamp = startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long endTimeStamp = endTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long timeSubPer = (endTimeStamp - startTimeStamp) / (parts == null ? 10 : parts);
                for (int i = 0; i < (parts == null ? 10 : parts); i++) {
                    timesList.add(new Times(startTime.plusSeconds(timeSubPer * i), startTime.plusSeconds(timeSubPer * (i + 1))));
                }
                break;
        }
        return timesList;
    }
}