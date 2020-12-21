package com.jwell56.security.cloud.service.apt.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_filed_setting")
public class SysFiledSetting extends Model<SysFiledSetting> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "filed_set_id",type= IdType.AUTO)
    private int filedSetId;

    private int userId;
    //设置类型 中文
    private String type;
    //自适应 0关闭 1开启
    private int selfAdaption;
    //多字段设置为字符串
    private String filed;

    private int page;

    private LocalDateTime createTime;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
