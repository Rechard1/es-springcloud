package com.jwell56.security.cloud.service.apt.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "搜索框查询对象",description = "搜索框查询对象 ")
public class ParamPI {
    @ApiModelProperty(value = "keyword", name = "全文检索",example = "")
    private String keyword;

    @ApiModelProperty(value = "sAreaIdList", name = "来源区域",example = "1,2,3")
    private String sAreaIdList;

    @ApiModelProperty(value = "sUnitIdList", name = "来源单位",example = "1,2,3")
    private String sUnitIdList;

    @ApiModelProperty(value = "sip", name = "来源ip",example = "192.168.1.1-192.168.1.255")
    private String sip;

    @ApiModelProperty(value = "smac", name = "来源mac",example = "")
    private String smac;

    @ApiModelProperty(value = "sport", name = "来源端口",example = "")
    private Integer sport;

    @ApiModelProperty(value = "dAreaIdList", name = "目的区域",example = "1,2,3")
    private String dAreaIdList;

    @ApiModelProperty(value = "dUnitIdList", name = "目的单位",example = "1,2,3")
    private String dUnitIdList;

    @ApiModelProperty(value = "dip", name = "目的ip",example = "192.168.1.1-192.168.1.255")
    private String dip;

    @ApiModelProperty(value = "dmac", name = "来源mac",example = "123")
    private String dmac;

    @ApiModelProperty(value = "dport", name = "目的端口",example = "123")
    private Integer dport;

    @ApiModelProperty(value = "riskGrade", name = "风险等级",example = "风险等级：高 中 低")
    private String riskGrade;

    @ApiModelProperty(value = "riskType", name = "风险类型",example = "")
    private String riskType;

    @ApiModelProperty(value = "deviceAreaIdList", name = "探针区域",example = "1,2,3")
    private String deviceAreaIdList;

    @ApiModelProperty(value = "deviceUnitIdList", name = "探针单位",example = "1,2,3")
    private String deviceUnitIdList;

    @ApiModelProperty(value = "deviceId", name = "探针设备",example = "")
    private Integer deviceId;

    @ApiModelProperty(value = "row", name = "日志原文",example = "")
    private String row;

    @ApiModelProperty(value = "timeParam", name = "时间",example = "")
    private TimeParam timeParam;

    @ApiModelProperty(value = "sum", name = "聚合",example = "")
    private boolean sum;

    @ApiModelProperty(value = "impAsset", name = "重要资产",example = "")
    private boolean impAsset;

    @ApiModelProperty(value = "outlandsAttack", name = "仅境外攻击",example = "")
    private boolean outlandsAttack;

    private int pageNum;

    private int pageSize;

    @ApiModelProperty(value = "enterpriseId", name = "企业id",example = "1")
    private int enterpriseId;

    private AreaUnit areaUnit;

    private String riskSort;

}
