package com.jwell56.security.cloud.service.netstruct.entity.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MappingStaticVo {

	private Integer mappingStaticId;
	
	private Integer sourceAreaId;
	
	private Integer mappingAreaId;
	
	private Integer enterpriseId;
	
    private Integer sourceUnitId;
	
	private Integer mappingUnitId;
	
	private Integer userId;
	
	private String sourceIp;
	
	private String mappingIp;
	
	private String sourceAreaName;
	
	private String mappingAreaName;
	
    private String sourceUnitName;
	
	private String mappingUnitName;
	
	private String userName;
	
	private String remark;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
    
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
}
