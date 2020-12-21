package com.jwell56.security.cloud.service.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
@TableName("sys_device")
public class SysDevice extends Model<SysDevice> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_id",type= IdType.AUTO)
    private int deviceId;

    /**
     * 设备名称
     */
    private String name;
    private String ip;
    private Integer areaId;
    private Integer unitId;

    /**
     * 设备类型：安全探针、防护设备、日志探针、反病毒、资产扫描
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




    @Override
    protected Serializable pkVal() {
        return null;
    }

}
