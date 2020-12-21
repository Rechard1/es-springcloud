package com.jwell56.security.cloud.service.netstruct.service.feign;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.entity.User;

@Component
public class UserComponent {

	@Autowired
	private IRoleService roleServcie;
	
	public String getUserName(Integer userId) {
		ResultObject res = roleServcie.getUserById(userId);
		Map user = (Map) res.getData();
		return (String) user.get("username");
	}
}
