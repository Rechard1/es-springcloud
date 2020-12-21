package com.jwell56.security.cloud.service.netstruct.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_mapping_dynamic")
public class MappingDynamic {

	@TableId(value = "mapping_dynamic_id",type= IdType.AUTO)
	private Integer mappingDynamicId;
	
	private Integer sourceAreaId;
	
	private Integer mappingAreaId;
	
    private Integer sourceUnitId;
	
	private Integer mappingUnitId;
	
	private Integer deviceId;
	
	private Integer enterpriseId;
	
	private Integer userId;
	
	private String sourceStartIp;
	
	private String sourceEndIp;
	
	private String mappingStartIp;
	
	private String mappingEndIp;
	
	private String remark;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
