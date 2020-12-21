package com.jwell56.security.cloud.service.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_device_snmp")
@AllArgsConstructor
@NoArgsConstructor
public class DeviceSnmp extends Model<DeviceSnmp>{

    private static final long serialVersionUID = 1L;
	
	@TableId(value = "device_id",type= IdType.AUTO)
    private int deviceId;
	
	private Integer assetId;
	
	private Integer areaId;
	
	private Integer unitId;
	
	private Integer settingId;
	
	private Integer probeId;
	
	private Integer port;
	
	private Integer state;
	
	private Integer userId;
	
	private Integer version;
	
	private String ip;
	
    private String name;
    
    private String deviceType;
    
    private String groupName;
    
    private Integer isUsed;
    
    private Integer enterpriseId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		// TODO Auto-generated method stub
		return null;
	}
}
