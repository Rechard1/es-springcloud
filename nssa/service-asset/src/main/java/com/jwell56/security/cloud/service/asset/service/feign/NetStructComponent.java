package com.jwell56.security.cloud.service.asset.service.feign;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.utils.RedisUtil;

/**
 * @author wsg
 * @since 2019/11/2
 */
@Component
public class NetStructComponent {
    @Autowired
    private INetStructService iNetStructService;
    
    @Autowired
    private RedisUtil redisUtil;

    public List<Integer> areaGetChildren(Integer areaId) {
        ResultObject areaResultObject = iNetStructService.areaGetChildren(areaId);
        List<Integer> areaIdList = (List<Integer>) areaResultObject.getData();
        return areaIdList;
    }

    public List<Integer> unitGetChildren(Integer unitId) {
        ResultObject unitResultObject = iNetStructService.unitGetChildren(unitId);
        List<Integer> unitIdList = (List<Integer>) unitResultObject.getData();
        return unitIdList;
    }
    
//    public String getAreaName(Integer areaId) {
//        ResultObject resultObject = iNetStructService.searchAreaById(areaId);
//        Map area = (Map) resultObject.getData();
//        return (String) area.get("name");
//    }
//
//    public String getUnitName(Integer unitId) {
//        ResultObject resultObject = iNetStructService.searchUnitById(unitId);
//        Map unit = (Map) resultObject.getData();
//        return (String) unit.get("name");
//    }
    
    public Integer getAreaId(String areaName, Integer enterpriseId) {
    	ResultObject res = iNetStructService.getAreaIdByName(areaName, enterpriseId);
    	return (Integer) res.getData();
    }
    
    public Integer getUnitId(String unitName, Integer enterpriseId) {
    	ResultObject res = iNetStructService.getUnitIdByName(unitName, enterpriseId);
    	return (Integer) res.getData();
    }
    
    public String getAreaName(Integer areaId) {
    	if(redisUtil.hasKey("areaName_" + areaId)) return (String) redisUtil.get("areaName_" + areaId);
    	ResultObject res = iNetStructService.getAreaName(areaId);
    	return (String) res.getData();
    }
    
    public String getUnitName(Integer unitId) {
    	if(redisUtil.hasKey("unitName_" + unitId)) return (String) redisUtil.get("unitName_" + unitId);
        ResultObject res = iNetStructService.getUnitName(unitId);
        return (String) res.getData();
    }
}
