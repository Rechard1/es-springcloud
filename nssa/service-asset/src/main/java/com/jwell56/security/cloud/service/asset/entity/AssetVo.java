package com.jwell56.security.cloud.service.asset.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2019/12/20
 */
@Data
public class AssetVo {
    @ApiModelProperty(example = "财务管理系统服务器", value = "资产名称")
    private String name;
}
