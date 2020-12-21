package com.jwell56.security.cloud.service.ids.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.ids.annotation.EntitySortOrder;
import com.jwell56.security.cloud.service.ids.annotation.FieldSettingProperty;
import com.jwell56.security.cloud.service.ids.annotation.TimeSearch;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author wsg
 * @since 2020/12/3
 */
@Data
public class IdsBaseInfo {
    @ApiModelProperty(value = "id")
    private String id;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @EntitySortOrder()
    @TimeSearch()
    @ApiModelProperty(value = "发生时间")
    @FieldSettingProperty(group = "time", isDefault = true)
    private LocalDateTime timestamp;

    @ApiModelProperty(value = "来源IP")
    @FieldSettingProperty(group = "s", isDefault = true)
    private String src_ip;

    @ApiModelProperty(value = "来源端口")
    @FieldSettingProperty(group = "s")
    private Integer src_port;

    @ApiModelProperty(value = "目的IP")
    @FieldSettingProperty(group = "d", isDefault = true)
    private String dest_ip;

    @ApiModelProperty(value = "目的端口")
    @FieldSettingProperty(group = "d")
    private Integer dest_port;

    @ApiModelProperty(value = "协议")
    @FieldSettingProperty(group = "base")
    private String proto;

    @ApiModelProperty(value = "源类型")
    @FieldSettingProperty(group = "base")
    private String source_type;

    @ApiModelProperty(value = "日志原文")
    @FieldSettingProperty(group = "base")
    private String raw;

    @ApiModelProperty(value = "应用协议")
    @FieldSettingProperty(group = "base", isDefault = true)
    private String app_proto;

    @ApiModelProperty(value = "设备id")
    @FieldSettingProperty(group = "base2")
    private String device_id;

    @ApiModelProperty(value = "事件源")
    @FieldSettingProperty(group = "base2")
    private String event_source;

    @ApiModelProperty(value = "事件类型")
    @FieldSettingProperty(group = "base2")
    private String event_type;

    @ApiModelProperty(value = "流量id")
    @FieldSettingProperty(group = "base2")
    private Long flow_id;

    @ApiModelProperty(value = "网口")
    @FieldSettingProperty(group = "base2")
    private String in_iface;
}
