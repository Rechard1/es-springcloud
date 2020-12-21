package com.jwell56.security.cloud.service.asset.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_asset_find_application")
@AllArgsConstructor
@NoArgsConstructor
public class AssetFindApplication {

	@TableId(value = "asset_find_application_id",type= IdType.AUTO)
	private Integer assetFindApplicationId;
	
	private Integer assetFindId;
	
	private String name;
	
	private String product;
	
	private String version;
	
	private String extrainfo;
	
	private Integer enterpriseId;
	
	private Long port;
	
	private Integer areaId;
	
	private Integer unitId;
	
	private String ip;
	
	private Integer probeId;
	
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
