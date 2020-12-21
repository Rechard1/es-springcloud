package com.jwell56.security.cloud.service.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_order")
public class Order extends Model<Order> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "order_id",type= IdType.AUTO)
    private Integer orderId;

    private Integer enterpriseId;

    private Integer riskId;

    private Integer assetId;

    private String assetName;

    private Integer areaId;

    private Integer unitId;

    private Integer createId;

    private Integer handlerId;

    private String handlerName;

    private String handlerPhone;

    private String handlerQq;

    private String orderType;

    private String orderName;

    private String orderStatus;

    private String riskGrade;

    private String messageSendWay;

    @ApiModelProperty(example = "0", value = "详情页面")
    private String fileName;

    @ApiModelProperty(example = "0", value = "处置时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime updateTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
