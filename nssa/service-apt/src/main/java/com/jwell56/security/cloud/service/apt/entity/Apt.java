package com.jwell56.security.cloud.service.apt.entity;

import com.alibaba.excel.metadata.BaseRowModel;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value="安全事件对象")
@TableName("base_apt") //对应表名
public class Apt extends BaseRowModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(notes = "时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime happenTime;

    private LocalDateTime createTime;

    private String sip;
    private String sport;
    private String smac;
    @JsonProperty("sipNum")
    private int sipNum;
    @JsonProperty("sAreaId")
    private int sAreaId;
    @JsonProperty("sUnitId")
    private int sUnitId;
    @JsonProperty("sAssetName")
    private String sAssetName;
    @JsonProperty("sAreaName")
    private String sAreaName;
    @JsonProperty("sUnitName")
    private String sUnitName;
    @JsonProperty("sDomain")
    private String sDomain;

    private SourceTargetInfo sourceTargetInfoS;

    private SourceTargetInfo sourceTargetInfoD;
    @JsonProperty("sAssetId")
    private int sAssetId;
    @JsonProperty("dAssetId")
    private int dAssetId;

    private String dip;
    private String dport;
    private String dmac;
    @JsonProperty("dIpNum")
    private int dIpNum;
    @JsonProperty("dAreaId")
    private int dAreaId;
    @JsonProperty("dUnitId")
    private int dUnitId;
    @JsonProperty("dAssetName")
    private String dAssetName;
    @JsonProperty("dAreaName")
    private String dAreaName;
    @JsonProperty("dUnitName")
    private String dUnitName;
    @JsonProperty("dDomain")
    private String dDomain;

    private int severity;
    private String riskGrade;
    private String riskType;
    private String riskDes;
    private String riskSuggestion;

    private String deviceName;
    private String deviceAreaName;
    private String deviceUnitName;
    private String deviceip;
    private String deviceIpNum;
    private int deviceid;
    private int deviceAreaId;
    private int deviceUnitId;

    private int assetId;
    private int enterpriseId;

    private int sipImportant;

    private int dipImportant;

    private int counts;

    private String rawLog;
    private String rowkey;


    private String riskSort;
    public static final String ATTACK_STAGE_HIGH="高";
    public static final String ATTACK_STAGE_MID="中";
    public static final String ATTACK_STAGE_LOW="低";
}
