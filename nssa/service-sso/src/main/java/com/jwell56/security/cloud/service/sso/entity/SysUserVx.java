package com.jwell56.security.cloud.service.sso.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 用户表
 * </p>
 *
 * @author wsg
 * @since 2019-10-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_user_vx")
public class SysUserVx extends Model<SysUserVx> {
    private static final long serialVersionUID = 1L;

    @TableId(value = "openid")
    private String openid;

    private Integer userid;

    private Integer send;

    @Override
    protected Serializable pkVal() {
        return null;
    }
}
