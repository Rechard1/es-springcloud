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

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_model_repository")
@AllArgsConstructor
@NoArgsConstructor
public class ModelRepositoryVo extends Model<ModelRepositoryVo> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "sys_model_repository_id",type= IdType.AUTO)
    private int sysModelRepositoryId;

    private String  modelType;

    private String  modelCode;

    private String  modelName;

    private String  modelDes;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

    private String  UserName;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
