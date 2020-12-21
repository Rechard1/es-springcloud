package com.jwell56.security.cloud.service.asset.entity.other;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * @author wsg
 * @since 2020/12/3
 */
public class IdsBaseInfo {
    @ApiModelProperty(value = "id")
    public String id;

    @ApiModelProperty(value = "目的IP", notes = "d", readOnly = true)
    public String dest_ip;

    @ApiModelProperty(value = "目的端口", notes = "d")
    public Integer dest_port;

    @ApiModelProperty(value = "协议", notes = "base")
    public String proto;

    @ApiModelProperty(value = "源类型", notes = "base")
    public String source_type;

    @ApiModelProperty(value = "来源IP", notes = "s", readOnly = true)
    public String src_ip;

    @ApiModelProperty(value = "来源端口", notes = "s")
    public Integer src_port;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    @ApiModelProperty(value = "发生时间", notes = "time", readOnly = true)
    public LocalDateTime timestamp;

    @ApiModelProperty(value = "日志原文", notes = "base")
    public String raw;
    
    @ApiModelProperty(value = "应用协议", notes = "base")
    public String app_proto;
    
}
