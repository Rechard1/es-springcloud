package com.jwell56.security.cloud.service.netstruct.service.feign;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.service.netstruct.entity.commons.NetstructOrder;

@Component
public class OrderComponent {

	@Autowired
	private IOrderService iOrderService;
	
	public Map getAssetDetail(LocalDateTime start,LocalDateTime end,Integer roleId,Integer enterpriseId,String areaLists,String unitLists) {
		NetstructOrder netstructOrder = new NetstructOrder(start, end, roleId,enterpriseId,areaLists, unitLists);
		Map resMap = (Map) iOrderService.getAssetDetail(netstructOrder).getData();
		return resMap;
	}
}
