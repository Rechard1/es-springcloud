package com.jwell56.security.cloud.service.asset.entity;

import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 资产表
 * </p>
 *
 * @author wsg
 * @since 2019-12-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BizAsset extends Model<BizAsset> {

    private static final long serialVersionUID = 1L;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 制造商
     */
    private String manufacturer;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 设备型号
     */
    private String model;

    /**
     * 类型
     */
    private String type;

    private String assetLevel;

    /**
     * IP、管理IP地址
     */
    private String ip;

    private Integer ipNum;

    /**
     * mac
     */
    private String mac;

    /**
     * 外网IP，映射IP
     */
    private String outerIp;

    private String outerPort;

    /**
     * 系统URL
     */
    private String sysUrl;

    /**
     * 物理位置
     */
    private String position;

    /**
     * 上线时间
     */
    private LocalDateTime startTime;

    /**
     * 预计过期时间
     */
    private LocalDateTime endTime;

    /**
     * 所属单位
     */
    private String department;

    /**
     * 上联
     */
    private String outBandwidth;

    /**
     * 下联
     */
    private String inBandwidth;

    /**
     * 镜像
     */
    private String mirror;

    /**
     * 安全接口
     */
    private String securityInterface;

    /**
     * 上联IP
     */
    private String upperLink;

    /**
     * 下联IP
     */
    private String lowerLink;

    /**
     * 是否报警
     */
    private String isAlarm;

    /**
     * 资产编号
     */
    private String assetNumber;

    /**
     * 功能说明
     */
    private String functionalDescription;

    /**
     * 资产登记
     */
    private String assetImportance;

    /**
     * 版本
     */
    private String version;

    /**
     * 操作系统
     */
    private String operatingSystem;

    /**
     * cpu信息
     */
    private String cpuInfo;

    /**
     * 内存大小
     */
    private String memorySize;

    /**
     * 硬盘大小
     */
    private String discSize;

    private String isSys;

    /**
     * 是否开启snmp
     */
    private String isOpenSnmp;

    /**
     * snmp版本
     */
    private String snmpVersion;

    /**
     * 中间件
     */
    private String middleware;

    private Integer oneAreaId;

    /**
     * 一级区域
     */
    private String oneArea;

    private Integer twoAreaId;

    /**
     * 二级区域
     */
    private String twoArea;

    private Integer areaId;

    private Integer unitId;

    /**
     * 管理员
     */
    private String manager;

    private String managerPhone;

    private String maintainer;

    private String maintainerPhone;

    /**
     * 技术参数
     */
    private String technicalParameter;

    /**
     * 域名
     */
    private String domain;

    private String isLegalCopy;

    private String databaseInfo;

    private String installTrendmicro;

    /**
     * 时间，创建时间
     */
    private LocalDateTime createTime;

    /**
     * 开放服务
     */
    private String openService;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
