package com.jwell56.security.cloud.service.role.entity;


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

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_loophole_vx")
public class LoopHoleVx extends Model<LoopHoleVx> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "loophole_vx_id",type= IdType.AUTO)
    private int loopholeVxId;

    private String type;

    private String pictureauto;

    private String url;

    private String cotent;

    private String grade;

    private String num;

    private String des;

    private String product;

    private String solution;

    private String patch;

    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private LocalDateTime bsTime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private LocalDateTime slTime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private LocalDateTime gxTime;
    @JsonFormat(pattern = "yyyy-MM-dd",timezone="GMT+8")
    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
