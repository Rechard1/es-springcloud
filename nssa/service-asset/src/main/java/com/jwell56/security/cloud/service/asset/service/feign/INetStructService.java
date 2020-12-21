package com.jwell56.security.cloud.service.asset.service.feign;

import com.jwell56.security.cloud.common.ResultObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wsg
 * @since 2019/11/1
 */
@FeignClient(value = "service-netstruct")
public interface INetStructService {
    @RequestMapping(value = "/area/getChildren", method = RequestMethod.GET)
    ResultObject areaGetChildren(@RequestParam("areaId") Integer areaId);

    @RequestMapping(value = "/unit/getChildren", method = RequestMethod.GET)
    ResultObject unitGetChildren(@RequestParam("unitId") Integer unitId);
    
    @RequestMapping(value = "/area/searchById", method = RequestMethod.GET)
    ResultObject searchAreaById(@RequestParam("areaId") Integer areaId);

    @RequestMapping(value = "/unit/searchById", method = RequestMethod.GET)
    ResultObject searchUnitById(@RequestParam("unitId") Integer unitId);
    
    @RequestMapping(value = "/area/getAreaIdByName", method = RequestMethod.GET)
    ResultObject getAreaIdByName(@RequestParam("areaName") String areaName, @RequestParam("enterpriseId") Integer enterpriseId);

    @RequestMapping(value = "/unit/getUnitIdByName", method = RequestMethod.GET)
    ResultObject getUnitIdByName(@RequestParam("unitName") String unitName, @RequestParam("enterpriseId") Integer enterpriseId);
    
    @RequestMapping(value = "/area/getAreaName", method = RequestMethod.GET)
    ResultObject getAreaName(@RequestParam("areaId") Integer areaId);
    
    @RequestMapping(value = "/unit/getUnitName", method = RequestMethod.GET)
    ResultObject getUnitName(@RequestParam("unitId") Integer unitId);
}
