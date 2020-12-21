package com.jwell56.security.cloud.service.netstruct.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.netstruct.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.netstruct.entity.Topology;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.service.IBigScreenSettingService;
import com.jwell56.security.cloud.service.netstruct.service.ITopologyService;
import com.jwell56.security.cloud.service.netstruct.service.IUnitService;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

@RestController
@RequestMapping("/bigscreen/netstruct")
public class BigScreenNetstructController {

	@Autowired
	private IBigScreenSettingService iBigScreenSettingService;
	
	@Autowired
	private ITopologyService iTopologyService;
	
	@Autowired
	private IAreaService iAreaService;
	
	@Autowired
	private IUnitService iUnitService;
	
	@GetMapping("detail")
	public ResultObject detail(Integer bigscreenId, String areaIdList, String unitIdList) {
        ResultObject res = new ResultObject<>();
        Map resMap = new HashMap();
        //获取用户信息
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	BigScreenSetting setting = iBigScreenSettingService.getById(bigscreenId);
      	if(areaIdList == null && unitIdList == null) {
      		String structure = setting.getStructure();
      		QueryWrapper<Topology> wrapper = new QueryWrapper<Topology>();
      		wrapper.lambda().eq(Topology :: getKeyName, structure);
      		Topology to = iTopologyService.getOne(wrapper);
      		resMap.put("areaName", iAreaService.getById(to.getAreaId()).getName());
      		resMap.put("unitName", iUnitService.getById(to.getUnitId()).getName());
      		resMap.put("fuzeren", "--");
      		resMap.put("remark", "--");
      		//以后添加
//      		areaIdList = to.getAreaId().toString();
//      		unitIdList = to.getUnitId().toString();
      	}
      	LocalDateTime start = setting.getTimeType() == 0 ? setting.getStart() : TimeUtil.getTime(setting.getTimeType()).get("start");
		LocalDateTime end = setting.getTimeType() == 0 ? setting.getEnd() : TimeUtil.getTime(setting.getTimeType()).get("end");
		Map detaiMap = iBigScreenSettingService.detail(start, end, userInfo, areaIdList, unitIdList);
		resMap.putAll(detaiMap);
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
		return res;
	}
}
