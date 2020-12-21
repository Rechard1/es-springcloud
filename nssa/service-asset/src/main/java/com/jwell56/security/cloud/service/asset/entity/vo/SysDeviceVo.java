package com.jwell56.security.cloud.service.asset.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 设备状态表
 * </p>
 *
 * @author wsg
 * @since 2019-11-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SysDeviceVo{

    private static final long serialVersionUID = 1L;

    private int deviceId;

    /**
     * 设备名称
     */
    private String name;

    private String ip;

    private Integer areaId;

    private Integer unitId;

    private String areaName;

    private String unitName;

    private String area;

    private String unit;

    /**
     * 设备类型：安全探针、防护设备、日志探针、反病毒设备
     */
    private String type;

    private String version;

    /**
     * 备注
     */
    private String remark;

    private String status;

    private String todayCount;

    private String todayFlow;

    private String username;

    private String password;

    private Integer userId;

    private Integer enterpriseId;


    /**
     * 最近同步时间
     */

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

//    /**
//     * 今日传输量
//     */
//    private String todayFlow;
//
//    /**
//     * 今日日志数
//     */
//    private String todayCount;
//    /**
//     * 状态：在线/离线
//     */
//    private String deviceStatus;
//
//    /**
//     * 设备名称，用于关联guard表的device_name
//     */
//    private String deviceKey;

}
