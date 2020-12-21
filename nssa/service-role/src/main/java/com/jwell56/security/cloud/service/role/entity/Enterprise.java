package com.jwell56.security.cloud.service.role.entity;

import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.role.validated.AddEnterprise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@TableName("sys_enterprise")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enterprise {

	@TableId(value = "enterprise_id",type= IdType.AUTO)
	private Integer enterpriseId;
	
	@NotEmpty(groups = {AddEnterprise.class})
	private String enterpriseName;
	
	private String remark;
	
	private Integer creatorId;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private LocalDateTime createTime;
}
