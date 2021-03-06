package com.jwell56.security.cloud.service.netstruct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_netstruct")
public class NetStruct extends Model<NetStruct> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "netstruct_id",type= IdType.AUTO)
    private int netstructId;

    private Integer areaId;

    private Integer unitId;
    
    private Integer enterpriseId;
    
    private Integer userId;

    private String startIp;

    private String endIp;

    private String remark;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
