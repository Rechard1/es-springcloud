package com.jwell56.security.cloud.common.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

/**
 * 根据时间设置，获取具体的时间参数
 *
 * @author wsg
 * @since 2019/4/10
 * TODO 用户如果选择自定义时间，并且选择的时间是自然日、周、月，则按照自然日周月来进行统计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeUtil {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public static final int TIME_TYPE_CUSTOM = 0;
    public static final int TIME_TYPE_30M = 1;
    public static final int TIME_TYPE_1H = 2;
    public static final int TIME_TYPE_12H = 3;
    public static final int TIME_TYPE_1D = 4;
    public static final int TIME_TYPE_7D = 5;
    public static final int TIME_TYPE_30D = 6;
    public static final int TIME_TYPE_YESTERDAY = 7;

    public static final int TIME_TYPE_NATIONAL = 101;//2019年国庆特殊参数
    private static final LocalDateTime nationalStart = FormatUtil.StringToDateTime("2019-10-01 00:00:00");
    private static final LocalDateTime nationalEnd = FormatUtil.StringToDateTime("2019-10-07 23:59:59");

    /**
     * 获取自然日、周、月的日期描述，非自然日、周、月返回空字符串
     */
    public static String getDateDes(LocalDateTime startTime, LocalDateTime endTime) {
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

    public static List<TimeUtil> getGroupDatetime(LocalDateTime endTime, LocalDateTime startTime, Integer timeType) {
        final int INDEX = 10;
        List<TimeUtil> timeUtilList = new ArrayList<>();
        timeType = timeType == null ? TIME_TYPE_1D : timeType;
        switch (timeType) {
            case TIME_TYPE_1D:
                for (int i = 0; i < 8; i++) {
                    if (startTime.plusHours(3 * (i + 1)).isAfter(endTime)) {
                        timeUtilList.add(new TimeUtil(startTime.plusHours(3 * i), endTime));
                        break;
                    } else {
                        timeUtilList.add(new TimeUtil(startTime.plusHours(3 * i), startTime.plusHours(3 * (i + 1))));
                    }
                }
                break;
            case TIME_TYPE_7D:
                for (int i = 0; i < 7; i++) {
                    if (startTime.plusDays(i + 1).isAfter(endTime)) {
                        timeUtilList.add(new TimeUtil(startTime.plusDays(i), endTime));
                        break;
                    } else {
                        timeUtilList.add(new TimeUtil(startTime.plusDays(i), startTime.plusDays(i + 1)));
                    }
                }
                break;
            case TIME_TYPE_30D:
                for (int i = 0; i < 10; i++) {
                    if (startTime.plusDays(3 * (i + 1)).isAfter(endTime)) {
                        timeUtilList.add(new TimeUtil(startTime.plusDays(3 * i), endTime));
                        break;
                    } else {
                        timeUtilList.add(new TimeUtil(startTime.plusDays(3 * i), startTime.plusDays(3 * (i + 1))));
                    }
                }
                break;
            case TIME_TYPE_NATIONAL:
                return getGroupDatetime(endTime, startTime, TIME_TYPE_7D);
            default:
                long startTimeStamp = startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long endTimeStamp = endTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long timeSubPer = (endTimeStamp - startTimeStamp) / INDEX;
                for (int i = 0; i < INDEX; i++) {
                    timeUtilList.add(new TimeUtil(startTime.plusSeconds(timeSubPer * i), startTime.plusSeconds(timeSubPer * (i + 1))));
                }
                break;
        }
        return timeUtilList;
    }

    public static List<Map<String, LocalDateTime>> getGroupDatetimeMapList(LocalDateTime endTime, LocalDateTime startTime, Integer timeType) {
        List<TimeUtil> timeUtilList = getGroupDatetime(endTime, startTime, timeType);
        List<Map<String, LocalDateTime>> list = new ArrayList<>();
        for (TimeUtil timeUtil : timeUtilList) {
            Map<String, LocalDateTime> timeMap = new HashMap<>();
            timeMap.put("startTime", timeUtil.getStartTime());
            timeMap.put("endTime", timeUtil.getEndTime());
            list.add(timeMap);
        }
        return list;
    }

    public static LocalDateTime getBeforeTime(Integer timeType, LocalDateTime time, LocalDateTime startTime, LocalDateTime endTime) {
        timeType = timeType == null ? TIME_TYPE_1D : timeType;
        switch (timeType) {
            case TIME_TYPE_CUSTOM:
                long startTimeStamp = startTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                long endTimeStamp = endTime.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() / 1000;
                return time.minusSeconds(endTimeStamp - startTimeStamp);
            case TIME_TYPE_30M:
                return time.minusMinutes(30);
            case TIME_TYPE_1H:
                return time.minusHours(1);
            case TIME_TYPE_12H:
                return time.minusHours(12);
            case TIME_TYPE_1D:
                return time.minusDays(1);
            case TIME_TYPE_7D:
                return time.minusDays(7);
            case TIME_TYPE_30D:
                return time.minusDays(30);
            case TIME_TYPE_YESTERDAY:
                return time.minusDays(1);
            case TIME_TYPE_NATIONAL:
                return time.minusDays(7);
            default:
                return time;
        }
    }

    public static LocalDateTime getEndTime(Integer timeType, String customEndTime) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (timeType == null) return null;
        if (timeType != null && timeType.equals(TIME_TYPE_CUSTOM) && customEndTime != null && !customEndTime.isEmpty()) {
            return LocalDateTime.parse(customEndTime, df);
        } else if (timeType != null && timeType.equals(TIME_TYPE_YESTERDAY)) {
            return LocalDateTime.of(LocalDate.now(), LocalTime.MIN).minusSeconds(1);
        } else if (timeType != null && timeType.equals(TIME_TYPE_NATIONAL)) {
            return nationalEnd;
        } else {
            return LocalDateTime.now();
        }
    }

    public static LocalDateTime getStartTime(Integer timeType, String customStartTime, LocalDateTime endTime) {
        LocalDateTime startTime;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (timeType == null) return null;
        //异常输入处理
        timeType = timeType == null ? TIME_TYPE_CUSTOM : timeType;
        if ((customStartTime == null || customStartTime.isEmpty()) && timeType.equals(TIME_TYPE_CUSTOM)) {
            timeType = TIME_TYPE_1D;
        }

        switch (timeType) {
            case TIME_TYPE_CUSTOM:
                startTime = LocalDateTime.parse(customStartTime, df);
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
                startTime = endTime;
                startTime = startTime.minusSeconds(startTime.getSecond());
                startTime = startTime.minusMinutes(startTime.getMinute());
                startTime = startTime.minusHours(startTime.getHour());
                break;
            case TIME_TYPE_7D:
                startTime = endTime.minusDays(6);
                startTime = startTime.minusSeconds(startTime.getSecond());
                startTime = startTime.minusMinutes(startTime.getMinute());
                startTime = startTime.minusHours(startTime.getHour());
                break;
            case TIME_TYPE_30D:
                startTime = endTime.minusDays(29);
                startTime = startTime.minusSeconds(startTime.getSecond());
                startTime = startTime.minusMinutes(startTime.getMinute());
                startTime = startTime.minusHours(startTime.getHour());
                break;
            case TIME_TYPE_YESTERDAY:
                startTime = endTime.minusDays(1);
                break;
            case TIME_TYPE_NATIONAL:
                return nationalStart;
            default:
                startTime = endTime;
                break;
        }
        return startTime;
    }

    public static boolean isToday(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime.toLocalDate().equals(LocalDate.now()) && endTime.toLocalDate().equals(LocalDate.now());
    }

    public static boolean containToday(LocalDateTime startTime, LocalDateTime endTime) {
        return !startTime.toLocalDate().equals(LocalDate.now()) && endTime.toLocalDate().equals(LocalDate.now());
    }

    public static boolean notToday(LocalDateTime startTime, LocalDateTime endTime) {
        return !startTime.toLocalDate().equals(LocalDate.now()) && !endTime.toLocalDate().equals(LocalDate.now());
    }

    public static String getShort(Integer timeType, LocalDateTime time) {
        String timeStr;
        switch (timeType) {
            case TIME_TYPE_30M:
                timeStr = time.toLocalTime().toString();
                break;
            case TIME_TYPE_1H:
                timeStr = time.toLocalTime().toString();
                break;
            case TIME_TYPE_12H:
                timeStr = time.toLocalTime().toString();
                break;
            case TIME_TYPE_1D:
                timeStr = time.toLocalTime().toString();
                break;
            case TIME_TYPE_7D:
                timeStr = time.toLocalDate().toString();
                break;
            case TIME_TYPE_30D:
                timeStr = time.toLocalDate().toString();
                break;
            case TIME_TYPE_YESTERDAY:
                timeStr = time.toLocalTime().toString();
                break;
            default:
                timeStr = time.toString();
                break;
        }
        return timeStr;
    }
    
    public static List<LocalDateTime> getDateList(LocalDateTime startTime, LocalDateTime endTime){
    	Duration duration = Duration.between(startTime,endTime);
    	long time = duration.toMillis();
    	time = time / 4000;
    	List<LocalDateTime> dateList = new ArrayList<LocalDateTime>();
    	dateList.add(startTime);
    	LocalDateTime lastTime = startTime.plusSeconds(time);
    	dateList.add(lastTime);
    	lastTime = lastTime.plusSeconds(time);
    	dateList.add(lastTime);
    	lastTime = lastTime.plusSeconds(time);
    	dateList.add(lastTime);
    	dateList.add(endTime);
    	return dateList;
    }
    
    public static Map<String, LocalDateTime> getTimeMap(Integer timeType, LocalDateTime start, LocalDateTime end){
    	Map<String, LocalDateTime> timeMap = new HashMap<String, LocalDateTime>();
    	switch (timeType) {
    	case 0:
    		Duration duration = Duration.between(start,end);
        	long time = duration.toMillis() / 1000;
    		timeMap.put("lastStart", start.minusSeconds(time));
        	timeMap.put("lastEnd", start);
    		break;
        case TIME_TYPE_30M:
            break;
        case TIME_TYPE_1H:
        	timeMap.put("lastStart", start.minusHours(1));
        	timeMap.put("lastEnd", end.minusHours(1));
            break;
        case TIME_TYPE_12H:
            break;
        case TIME_TYPE_1D:
        	timeMap.put("lastStart", start.minusDays(1));
        	timeMap.put("lastEnd", end.minusDays(1));
            break;
        case TIME_TYPE_7D:
        	timeMap.put("lastStart", start.minusWeeks(1));
        	timeMap.put("lastEnd", end.minusWeeks(1));
            break;
        case TIME_TYPE_30D:
        	timeMap.put("lastStart", start.minusMonths(1));
        	timeMap.put("lastEnd", end.minusMonths(1));
            break;
        case TIME_TYPE_YESTERDAY:
            break;
        default:
            break;
    }
    	return timeMap;
    }
    
    //时间特殊处理
    public static String format(LocalDateTime time) {
    	String timeString = time.toString();
    	String t = timeString.replace("T", " ");
    	Integer size = t.getBytes().length;
    	if(size == 16) {
    		t = t + ":00";
    	}
    	
    	if(size > 20) {
    		t = t.substring(0,19);
    	}
    	return t;
    }
    
    public static Map<String, LocalDateTime> getTime(Integer timeType){
    	Map<String, LocalDateTime> timeMap = new HashMap<String, LocalDateTime>();
    	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    	String string = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
        LocalDateTime localDay = LocalDateTime.parse(string +" "+ "00:00:00", df);
    	if(timeType == 2) {
			timeMap.put("end", LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),df));
			timeMap.put("start", timeMap.get("end").minusHours(1));
		}
    	else if(timeType == 4) {
			timeMap.put("end", LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),df));
			timeMap.put("start", localDay);
		}
    	else if(timeType == 5) {
			timeMap.put("end", localDay);
			timeMap.put("start", timeMap.get("end").minusWeeks(1));
		}
    	else if(timeType == 6) {
			timeMap.put("end", localDay);
			timeMap.put("start", timeMap.get("end").minusMonths(1));
		}
    	else {
    		timeMap.put("end", LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),df));
			timeMap.put("start", LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),df));
		}
    	return timeMap;
    }
}
