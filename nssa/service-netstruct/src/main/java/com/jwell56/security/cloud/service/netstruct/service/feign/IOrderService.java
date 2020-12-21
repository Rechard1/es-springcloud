package com.jwell56.security.cloud.service.netstruct.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.entity.commons.NetstructOrder;

@FeignClient("service-order")
public interface IOrderService {

	@PostMapping("/bigscreen/order/getAssetDetail")
	public ResultObject getAssetDetail(@RequestBody NetstructOrder netstructOrder);
}
