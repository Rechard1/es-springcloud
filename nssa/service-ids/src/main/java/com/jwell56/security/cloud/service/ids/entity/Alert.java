package com.jwell56.security.cloud.service.ids.entity;

import java.time.LocalDateTime;

import com.jwell56.security.cloud.service.ids.annotation.FieldSettingProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.ids.annotation.EntitySortOrder;
import com.jwell56.security.cloud.service.ids.annotation.TimeSearch;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/2
 */
@Data
@TableName("bs-core-alert-*")
public class Alert extends IdsBaseInfo{
    @ApiModelProperty(value = "告警动作")
    @FieldSettingProperty(group = "alert", isDefault = true)
    private String alert__action;

    @ApiModelProperty(value = "风险类型")
    @FieldSettingProperty(group = "alert", isDefault = true)
    private String alert__category;

    @ApiModelProperty(value = "风险描述")
    @FieldSettingProperty(group = "alert")
    private String alert__signature;

    @ApiModelProperty(value = "风险等级")
    @FieldSettingProperty(group = "alert", isDefault = true)
    private Integer alert__severity;

    @ApiModelProperty(value = "alert.gid")
    @FieldSettingProperty(group = "alert")
    private Integer alert__gid;

    @ApiModelProperty(value = "alert.rev")
    @FieldSettingProperty(group = "alert")
    private Integer alert__rev;

    @ApiModelProperty(value = "alert.signature_id")
    @FieldSettingProperty(group = "alert")
    private Integer alert__signature_id;

    @ApiModelProperty(value = "payload")
    @FieldSettingProperty(group = "alert")
    private String payload_printable;

    @ApiModelProperty(value = "主机名")
    @FieldSettingProperty(group = "http")
    private String http__hostname;

    @ApiModelProperty(value = "方法")
    @FieldSettingProperty(group = "http")
    private String http__http_method;

    @ApiModelProperty(value = "请求内容")
    @FieldSettingProperty(group = "http")
    private String http__http_request_body_printable;

    @ApiModelProperty(value = "用户代理")
    @FieldSettingProperty(group = "http")
    private String http__http_user_agent;

    @ApiModelProperty(value = "请求长度")
    @FieldSettingProperty(group = "http")
    private Integer http__length;

    @ApiModelProperty(value = "http协议")
    @FieldSettingProperty(group = "http")
    private String http__protocol;

    @ApiModelProperty(value = "url")
    @FieldSettingProperty(group = "http")
    private String http__url;

    @ApiModelProperty(value = "tx_id")
    @FieldSettingProperty(group = "http")
    private Integer tx_id;
}
