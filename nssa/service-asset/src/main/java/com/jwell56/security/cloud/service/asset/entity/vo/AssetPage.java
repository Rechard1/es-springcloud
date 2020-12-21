package com.jwell56.security.cloud.service.asset.entity.vo;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssetPage {

    private int assetId;
	
//    private Integer ipNum;
    
    private Integer areaId;
    
    private Integer unitId;
    
    private Integer important;
    
    private Integer userId;
    
    private Integer assetFindId;
    
    private String areaName;
    
    private String unitName;
    
    private String importantType;
    
    private String type;
    
    private String mac;
    
    private String principal;
    
    private String principalPhone;
    
    private String expandInfo;
    
    private String phyAddress;
    
    private String name;
    
    private String ip;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
