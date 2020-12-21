package com.jwell56.security.cloud.service.role.entity;

import javax.validation.constraints.NotEmpty;

import com.jwell56.security.cloud.service.role.validated.ModifyUser;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModifyPasswordEntity {

	@NotEmpty(groups = {ModifyUser.class})
	private String oldPassword;
	
	@NotEmpty(groups = {ModifyUser.class})
	private String newPassword;

	@NotEmpty(groups = {ModifyUser.class})
    private String confirmPassword;
}
