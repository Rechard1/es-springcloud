package com.jwell56.security.cloud.service.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_home_setting")
@AllArgsConstructor
@NoArgsConstructor
public class HomeSetting extends Model<HomeSetting>{

    private static final long serialVersionUID = 1L;
	
	@TableId(value = "user_id")
    private Integer userId;

    @ApiModelProperty(example = "1,2", value = "0基本首页模板1未处理工单、2工单统计、3统计报告、4设备状态、5安全日报、6紧急通知")
	private String module;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
