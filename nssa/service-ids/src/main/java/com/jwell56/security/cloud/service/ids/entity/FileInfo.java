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
@TableName("bs-core-fileinfo-*")
public class FileInfo extends IdsBaseInfo {
    @ApiModelProperty(value = "文件名")
    @FieldSettingProperty(group = "fileinfo", isDefault = true)
    private String fileinfo__filename;

    @ApiModelProperty(value = "文件保存")
    @FieldSettingProperty(group = "fileinfo")
    private Boolean fileinfo__stored;

    @ApiModelProperty(value = "文件大小")
    @FieldSettingProperty(group = "fileinfo", isDefault = true)
    private Integer fileinfo__size;

    @ApiModelProperty(value = "文件状态")
    @FieldSettingProperty(group = "fileinfo", isDefault = true)
    private String fileinfo__state;

    @ApiModelProperty(value = "fileinfo.gaps")
    @FieldSettingProperty(group = "fileinfo")
    private Boolean fileinfo__gaps;

    @ApiModelProperty(value = "fileinfo.tx_id")
    @FieldSettingProperty(group = "fileinfo")
    private Integer fileinfo__tx_id;

    @ApiModelProperty(value = "主机名")
    @FieldSettingProperty(group = "http")
    private String http__hostname;

    @ApiModelProperty(value = "方法")
    @FieldSettingProperty(group = "http")
    private String http__http_method;

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
}
