package com.jwell56.security.cloud.service.netstruct.entity.vo;

import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2019/11/18
 */
@Data
public class TopologyVo {

    @ApiModelProperty(example = "鞍钢集团", value = "拓扑图名称")
    private String name;

    @ApiModelProperty(example = "angang", value = "节点参数")
    private String keyName;

    @ApiModelProperty(example = "1,2,3", value = "拓扑图对应区域id，多个id以,号隔开")
    private String areaIdList;

    @ApiModelProperty(example = "1,2,3", value = "拓扑图对应单位id，多个id以,号隔开")
    private String unitIdList;

    @ApiModelProperty(example = "1", value = "拓扑节点威胁等级，数字越大威胁越高，0安全，1低危，2中危，3高危，4紧急", allowableValues = "0,1,2,3,4")
    private Integer level;

    @ApiModelProperty(example = "true", value = "该节点用户是否有权限", allowableValues = "true,false")
    private Boolean access;

    @ApiModelProperty(example = "true", value = "是否包含子节点", allowableValues = "true,false")
    private Boolean child;

    @ApiModelProperty(value = "报警消息")
    private List<AlertVo> alert;

}
