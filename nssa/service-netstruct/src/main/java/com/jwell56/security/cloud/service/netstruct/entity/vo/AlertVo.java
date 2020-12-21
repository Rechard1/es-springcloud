package com.jwell56.security.cloud.service.netstruct.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2019/11/18
 */
@Data
public class AlertVo {

    @ApiModelProperty(example = "1", value = "消息id")
    private Integer id;

    @ApiModelProperty(example = "动能区服务器", value = "设备信息")
    private String asset;

    @ApiModelProperty(example = "smb攻击", value = "报警描述")
    private String des;

    @ApiModelProperty(example = "1", value = "等级：1低危，2中危，3高危", allowableValues = "1,2,3")
    private Integer grade;

    @ApiModelProperty(example = "apt", value = "类型:apt威胁,anomaly异常", allowableValues = "apt,anomaly")
    private String type;
}
