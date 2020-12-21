package com.jwell56.security.cloud.service.apt.entity.bo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDeviceBo {
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
     * 设备类型：安全探针、防护设备、日志探针、反病毒
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

}
