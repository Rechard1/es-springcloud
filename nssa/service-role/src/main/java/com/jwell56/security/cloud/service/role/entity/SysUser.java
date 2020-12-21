package com.jwell56.security.cloud.service.role.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.role.validated.AddUser;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@TableName("sys_user")
public class SysUser extends Model<SysUser> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;

    private Integer age;

    /**
     * 用户名
     */
    @NotEmpty(groups = {AddUser.class})
    private String username;

    @NotEmpty(groups = {AddUser.class})
    private String password;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 生产区id
     */
//    private Integer areaCode;

    private Integer roleId;

    /**
     * 生产区名称
     */
//    private String areaName;

    private String phoneNum;

    private String email;

    private String remark;
    
    private Integer enterpriseId;
    
    private Integer creatorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private Integer roleType;
    
    private Integer enterpriseFlag;
    
//    private Integer adminFlag;

    @Override
    protected Serializable pkVal() {
        return null;
    }

}
