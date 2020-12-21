package com.jwell56.security.cloud.service.role.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.role.entity.Enterprise;

public interface IEnterpriseService extends IService<Enterprise>{
	
	void delete(Integer enterpriseId);

}
