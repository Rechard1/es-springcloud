package com.jwell56.security.cloud.service.netstruct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
    private String deviceName;

    /**
     * 设备类型：安全探针、防护设备、服务器、系统设备
     */
    private String deviceType;

    private String ip;

    private Integer areaId;

    private Integer unitId;

    private Integer userId;

    private Integer enterpriseId;

    /**
     * 今日传输量
     */
    private String todayFlow;

    /**
     * 今日日志数
     */
    private String todayCount;

    /**
     * 最近同步时间
     */
    private LocalDateTime updateTime;
    private LocalDateTime createTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态：在线/离线
     */
    private String deviceStatus;

    /**
     * 设备名称，用于关联guard表的device_name
     */
    private String deviceKey;

    private String username;

    private String password;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
