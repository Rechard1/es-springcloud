package com.jwell56.security.cloud.service.asset.entity.other;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/2
 */
@Data
@TableName("bs-core-alert-*")
public class Alert extends IdsBaseInfo {

    @ApiModelProperty(value = "告警动作", notes = "info", readOnly = true)
    public String alert__action;

    @ApiModelProperty(value = "风险类型", notes = "info", readOnly = true)
    public String alert__category;

    @ApiModelProperty(value = "风险描述")
    public String alert__signature;

    @ApiModelProperty(value = "风险等级", notes = "info", readOnly = true)
    public Integer alert__severity;
}
