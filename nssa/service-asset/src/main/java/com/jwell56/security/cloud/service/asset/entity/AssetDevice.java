package com.jwell56.security.cloud.service.asset.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_asset_device")
@AllArgsConstructor
@NoArgsConstructor
public class AssetDevice extends Model<AssetDevice> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "asset_device_id",type= IdType.AUTO)
    private int assetDeviceId;
    @NonNull
    private Integer deviceId;
    @NonNull
    private Integer assetId;

    private Integer userId;

    private Integer enterpriseId;

    private String ip;

    private String logtype;

    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    private String status;

    private String todayCount;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
