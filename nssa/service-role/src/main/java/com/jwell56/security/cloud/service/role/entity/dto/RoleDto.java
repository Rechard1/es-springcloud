package com.jwell56.security.cloud.service.role.entity.dto;

import javax.validation.Valid;

import com.jwell56.security.cloud.service.role.entity.Role;

import lombok.Data;

@Data
public class RoleDto {
	
    private String unitList; 
    private String areaList; 
    private String moduleList;

    @Valid
    private Role role;
}
