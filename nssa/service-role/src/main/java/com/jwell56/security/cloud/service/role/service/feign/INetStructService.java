package com.jwell56.security.cloud.service.role.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.jwell56.security.cloud.common.ResultObject;

/**
 * @author wsg
 * @since 2019/11/1
 */
@FeignClient(value = "service-netstruct")
public interface INetStructService {
    
    @RequestMapping(value = "/area/getAreaByEnterpriseId", method = RequestMethod.GET)
    ResultObject getAreaByEnterpriseId(@RequestParam("enterpriseId") Integer enterpriseId);

    @RequestMapping(value = "/unit/getUnitByEnterpriseId", method = RequestMethod.GET)
    ResultObject getUnitByEnterpriseId(@RequestParam("enterpriseId") Integer enterpriseId);
}
