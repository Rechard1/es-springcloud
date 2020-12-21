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
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_device_iac_setting")
@AllArgsConstructor
@NoArgsConstructor
public class DeviceIacSetting extends Model<DeviceIacSetting> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "device_iac_setting_id",type= IdType.AUTO)
    private int deviceIacSettingId;

    private Integer deviceId;

    private String serverIp;

    private String clientIp;

    private String file;

    private Integer logOpen;

    private String logIp;

    private String logPort;

    private Integer fileCheck;

    private Integer enterpriseId;

    private Integer userId;

    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
