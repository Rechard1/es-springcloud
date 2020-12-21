package com.jwell56.security.cloud.service.asset.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@TableName("sys_bigscreen_setting")
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BigScreenSetting extends Model<BigScreenSetting>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@TableId(value = "bigscreen_id",type= IdType.AUTO)
	private Integer bigscreenId;
	
	private Integer carouselFlag;
	
	private Integer timeType;
	
	private Integer userId;
	
	private Integer enterpriseId;
	
	private String bigscreenName;
	
	private String structure;
	
	private String statisticsLeft;
	
	private String statisticsRight;
	
	private String detail;
	
    //任务时间范围--开始
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime start;
    
    //任务时间范围--结束
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime end;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

	@Override
	protected Serializable pkVal() {
		// TODO Auto-generated method stub
		return null;
	}
}
