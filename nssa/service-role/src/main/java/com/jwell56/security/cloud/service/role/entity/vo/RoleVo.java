package com.jwell56.security.cloud.service.role.entity.vo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RoleVo {

    private int roleId;
    
    private Integer creatorId;

    private String roleName;

    private String roleDesc;
    
    private Integer roleType;
    
    private Integer enterpriseId;
    
    //创建人
    private String userName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private LocalDateTime createTime;
}
