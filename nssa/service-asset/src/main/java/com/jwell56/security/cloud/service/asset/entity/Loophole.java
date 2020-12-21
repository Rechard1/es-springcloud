package com.jwell56.security.cloud.service.asset.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_cnvd_information")
@AllArgsConstructor
@NoArgsConstructor
public class Loophole extends Model<Loophole> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "cnvd_id")
    private String cnvdId;

    private LocalDateTime reportingTime;

    private String grade;

    private String loopholeType;

    private Integer userId;

    private Integer enterpriseId;

    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
