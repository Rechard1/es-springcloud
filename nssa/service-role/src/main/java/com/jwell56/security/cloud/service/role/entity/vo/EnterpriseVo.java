package com.jwell56.security.cloud.service.role.entity.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EnterpriseVo {

    private Integer enterpriseId;
	
	private String enterpriseName;
	
	private String remark;
	
	private Integer creatorId;
	
	private String creatorName;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
	private LocalDateTime createTime;
}
