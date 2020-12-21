package com.jwell56.security.cloud.service.netstruct.entity.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappingDynamicVo {

	private Integer mappingDynamicId;
	
	private Integer sourceAreaId;
	
	private Integer mappingAreaId;
	
	private Integer deviceId;
	
	private Integer enterpriseId;
	
	private Integer userId;
	
	private String sourceStartIp;
	
	private String sourceEndIp;
	
	private String mappingStartIp;
	
	private String mappingEndIp;
	
    private String sourceAreaName;
	
	private String mappingAreaName;
	
	private String deviceName;
	
	private String remark;
	
	private String userName;
	
    private Integer sourceUnitId;
	
	private Integer mappingUnitId;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
}
