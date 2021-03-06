package com.jwell56.security.cloud.service.asset.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_device_monitor_area")
@AllArgsConstructor
@NoArgsConstructor
public class SysDeviceMonitorArea extends Model<SysDeviceMonitorArea> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_monitor_area_id",type= IdType.AUTO)
    private int deviceMonitorAreaId;

    private Integer deviceId;

    private Integer areaId;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
