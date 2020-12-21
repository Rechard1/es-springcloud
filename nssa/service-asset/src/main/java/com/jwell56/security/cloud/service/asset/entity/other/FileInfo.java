package com.jwell56.security.cloud.service.asset.entity.other;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/2
 */
@Data
@TableName("bs-core-fileinfo-*")
public class FileInfo extends IdsBaseInfo {
    @ApiModelProperty(value = "文件名", notes = "info", readOnly = true)
    public Integer fileinfo__filename;

    @ApiModelProperty(value = "文件保存", notes = "info", readOnly = true)
    public Integer fileinfo__stored;

    @ApiModelProperty(value = "文件大小", notes = "info", readOnly = true)
    public Integer fileinfo__size;

    @ApiModelProperty(value = "文件状态", notes = "info", readOnly = true)
    public Integer fileinfo__state;
}
