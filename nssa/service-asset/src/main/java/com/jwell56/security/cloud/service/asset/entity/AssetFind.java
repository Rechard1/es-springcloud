package com.jwell56.security.cloud.service.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

/**
 * <p>
 * 
 * </p>
 *
 * @author RonnieXu
 * @since 2019-03-22
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_asset_find")
public class AssetFind extends Model<AssetFind> implements Serializable{

    private static final long serialVersionUID = 1L;

    //对应id，可不填
    @TableId(value = "asset_find_id",type= IdType.AUTO)
    private int assetFindId;

    /**
     * 设备名称
     */
    private String name;

    /**
     * 类型
     */
    private String type;

    /**
     * IP、管理IP地址
     */
    private String ip;


    /**
     * 所属单位
     */
    private Integer unitId;

    private Integer areaId;
    
    /**
     * mac
     */
    private String mac;

    /**
     * 关联sys_asset.id,为0表示未登记
     */
    private Integer assetId;

//    /**
//     * 操作系统
//     */
//    private String operatingSystem;
    
    private Integer probeId;
//
    private Integer enterpriseId;
    
    private Integer userId;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
