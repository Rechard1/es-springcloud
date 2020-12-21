package com.jwell56.security.cloud.service.apt.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.apt.utils.FormatUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * @author wsg
 * @since 2019/8/30
 */
@Data
public class TimeParam {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(example = "2019-08-27 00:00:00", value = "开始时间", hidden = true)
    private LocalDateTime startTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty(example = "2019-09-02 00:00:00", value = "结束时间", hidden = true)
    private LocalDateTime endTime;

    @ApiModelProperty(example = "2019-08-27 00:00:00", value = "开始时间")
    private String customStartTime;

    @ApiModelProperty(example = "2019-09-02 00:00:00", value = "结束时间")
    private String customEndTime;

    @ApiModelProperty(example = "6", value = "时间范围类型,0:自定义,1:30分钟,2:1小时,3:12小时,4:1天,5:7天,6:30天", allowableValues = "0,1,2,3,4,5,6")
    private Integer timeType;

    public TimeParam() {

    }

    public TimeParam(Integer timeType, String customStartTime, String customEndTime) {
        this.setTimeType(timeType);
        this.setCustomStartTime(customStartTime);
        this.setCustomEndTime(customEndTime);
    }

    public void setCustomStartTime(String customStartTime) {
        this.customStartTime = customStartTime;
        this.startTime = FormatUtils.StringToDateTime(customStartTime);
    }

    public void setCustomEndTime(String customEndTime) {
        this.customEndTime = customEndTime;
        this.endTime = FormatUtils.StringToDateTime(customEndTime);
    }

    public LocalDateTime getEndTime() {
        return TimeUtil.getEndTime(timeType, customEndTime);
    }

    public LocalDateTime getStartTime() {
        return TimeUtil.getStartTime(timeType, customStartTime, getEndTime());
    }

    public LocalDateTime hisEnd() {
        return TimeUtil.getBeforeTime(timeType, this.getEndTime(), this.getStartTime(), this.getEndTime());
    }

    public LocalDateTime hisStart() {
        return TimeUtil.getBeforeTime(timeType, this.getStartTime(), this.getStartTime(), this.getEndTime());
    }

    public List<TimeUtil> group() {
        return TimeUtil.getGroupDatetime(this.getEndTime(), this.getStartTime(), this.getTimeType());
    }

    public List<TimeUtil> hisGroup() {
        return TimeUtil.getGroupDatetime(this.hisEnd(), this.hisStart(), this.getTimeType());
    }

    public boolean useCache() {
        return this.timeType != null && (this.timeType == 5 || this.timeType == 6);
    }

    public String getDes() {
        String dateDes = TimeUtil.getDateDes(this.getStartTime(), this.getEndTime());
        if (!dateDes.isEmpty()) {
            return dateDes;
        } else {
            DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return df.format(this.getStartTime()) + "至" + df.format(this.getEndTime());
        }
    }
}
