package com.jwell56.security.cloud.service.ids.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.ids.annotation.EntitySortOrder;
import com.jwell56.security.cloud.service.ids.annotation.FieldSettingProperty;
import com.jwell56.security.cloud.service.ids.annotation.TimeSearch;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author wsg
 * @since 2020/12/2
 */
@Data
@TableName("bs-core-flow-*")
public class Flow extends IdsBaseInfo {
    @ApiModelProperty(value = "接收流量")
    @FieldSettingProperty(group = "info", isDefault = true)
    private Integer flow__bytes_toclient;

    @ApiModelProperty(value = "发送流量")
    @FieldSettingProperty(group = "info", isDefault = true)
    private Integer flow__bytes_toserver;

    @ApiModelProperty(value = "接收数据包")
    @FieldSettingProperty(group = "info", isDefault = true)
    private Integer flow__pkts_toclient;

    @ApiModelProperty(value = "发送数据包")
    @FieldSettingProperty(group = "info", isDefault = true)
    private Integer flow__pkts_toserver;

    @ApiModelProperty(value = "flow.age")
    @FieldSettingProperty(group = "info2")
    private Integer flow__age;

    @ApiModelProperty(value = "flow.alerted")
    @FieldSettingProperty(group = "info2")
    private Boolean flow__alerted;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty(value = "发生时间")
    @FieldSettingProperty(group = "info2")
    private LocalDateTime flow__end;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty(value = "发生时间")
    @FieldSettingProperty(group = "info2")
    private LocalDateTime flow__start;

    @ApiModelProperty(value = "flow.reason")
    @FieldSettingProperty(group = "info2")
    private String flow__reason;

    @ApiModelProperty(value = "flow.state")
    @FieldSettingProperty(group = "info2")
    private String flow__state;
}
