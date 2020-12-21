package com.jwell56.security.cloud.service.role.entity.vo;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.role.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user") //对应表名
public class UserVo implements Serializable {

    private Integer userId;

    private Integer age;

    private String username;

    private String password;

    private String realName;

    private Integer areaCode;

    private String email;

    private String phoneNum;

    private String areaName;

    private Integer roleId;
    
    private Integer enterpriseId;
    
    private Integer creatorId;
    
    private String enterpriseName;

    private Role role;

    private String remark;
    
    private Integer roleType;
    
    private Integer enterpriseFlag;
    
    private Integer adminFlag;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;

}
