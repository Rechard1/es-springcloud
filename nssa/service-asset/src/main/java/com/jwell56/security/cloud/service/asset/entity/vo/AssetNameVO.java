package com.jwell56.security.cloud.service.asset.entity.vo;

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

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_asset")
@AllArgsConstructor
@NoArgsConstructor
public class AssetNameVO extends Model<AssetNameVO>{

private static final long serialVersionUID = 1L;
	
	@TableId(value = "asset_id",type= IdType.AUTO)
    private int assetId;
    
    private Integer areaId;

    private String areaName;
    
    private Integer unitId;

    private String unitName;
    
    private Integer important;
    
    private Integer userId;
    
    private String type;
    
    private String mac;
    
    private String principal;
    
    private String principalPhone;
    
    private String expandInfo;
    
    private String phyAddress;
    
    private String name;
    
    private String ip;
    
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
