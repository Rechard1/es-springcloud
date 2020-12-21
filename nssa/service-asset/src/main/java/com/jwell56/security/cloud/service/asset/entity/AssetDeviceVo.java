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
@AllArgsConstructor
@NoArgsConstructor
public class AssetDeviceVo  implements Serializable {

    private static final long serialVersionUID = 1L;

    private int assetDeviceId;

    private Integer deviceId;

    private Integer assetId;

    private Integer userId;

    private Integer enterpriseId;

    private String ip;

    private String logtype;

    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    private String assetName;

    private String areaName;

    private String unitName;

    private String status;

    private String todayCount;

}
