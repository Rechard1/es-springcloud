package com.jwell56.security.cloud.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import net.sf.cglib.core.Local;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_risk")
public class Risk extends Model<Risk> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "risk_id",type= IdType.AUTO)
    private int riskId;

    private Integer userId;

    private Integer enterpriseId;

    private String device;
    //知识库
    private String riskType;
    //风险名称
    private String riskName;
    //风险分类
    private String riskClass;

    private String riskDes;

    private String riskSuggestion;

    private String riskGrade;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
