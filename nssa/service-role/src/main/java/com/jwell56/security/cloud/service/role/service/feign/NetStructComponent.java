package com.jwell56.security.cloud.service.role.service.feign;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.common.ResultObject;

/**
 * @author wsg
 * @since 2019/11/2
 */
@Component
public class NetStructComponent {
    
	@Autowired
    private INetStructService iNetStructService;
    
    public List<Integer> getAreaByEnterpriseId(Integer enterpriseId) {
        ResultObject areaResultObject = iNetStructService.getAreaByEnterpriseId(enterpriseId);
        List<Integer> areaIdList = (List<Integer>) areaResultObject.getData();
        return areaIdList;
    }

    public List<Integer> getUnitByEnterpriseId(Integer enterpriseId) {
        ResultObject unitResultObject = iNetStructService.getUnitByEnterpriseId(enterpriseId);
        List<Integer> unitIdList = (List<Integer>) unitResultObject.getData();
        return unitIdList;
    }
}
