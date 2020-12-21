package com.jwell56.security.cloud.service.netstruct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.jwell56.security.cloud.service.netstruct.common.IPUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
@TableName("sys_intranet")
@AllArgsConstructor
@NoArgsConstructor
public class Intranet extends Model<Intranet> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "intranet_id",type= IdType.AUTO)
    private int intranetId;
    
    private Integer enterpriseId;
    
    private Integer userId;
    
    private String startIp;

    private String endIp;

    private String remark;
    
    private Long startipNum;
    
    private Long endipNum;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    public Long startIpNum(){
        return IPUtil.ipToLong(this.startIp);
    }

    public Long endIpNum(){
        return IPUtil.ipToLong(this.endIp);
    }

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
