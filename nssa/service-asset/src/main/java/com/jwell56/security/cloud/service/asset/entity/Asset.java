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

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_asset")
@AllArgsConstructor
@NoArgsConstructor
public class Asset extends Model<Asset>{

private static final long serialVersionUID = 1L;
	
	@TableId(value = "asset_id",type= IdType.AUTO)
    private Integer assetId;
	
//    private Integer ipNum;
    
    private Integer areaId;
    
    private Integer unitId;
    
    private Integer assetFindId;
    
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

    public static final String TYPE_SERVER = "服务器";
    public static final String TYPE_SECURITY = "安全设备";
    public static final String TYPE_NETDEV = "网络设备";
    public static final String TYPE_COMPUTER = "终端";
    public static final String TYPE_ROUTER = "路由器";
    public static final String TYPE_INTERCHANGER = "交换机";
}
