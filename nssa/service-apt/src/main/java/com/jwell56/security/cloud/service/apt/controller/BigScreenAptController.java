package com.jwell56.security.cloud.service.apt.controller;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.apt.entity.Apt;
import com.jwell56.security.cloud.service.apt.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.apt.entity.User;
import com.jwell56.security.cloud.service.apt.service.IBigScreenSettingService;
import com.jwell56.security.cloud.service.apt.utils.ThreadLocalUtil;

@RestController
@RequestMapping("/bigscreen/apt")
public class BigScreenAptController {

	@Autowired
	private IBigScreenSettingService iBigScreenSettingService;
	
	@GetMapping("getZongHe")
	public ResultObject getZongHe(Integer bigscreenId, String areaIdList, String unitIdList) {
		ResultObject res = new ResultObject<>();
		BigScreenSetting setting = iBigScreenSettingService.getById(bigscreenId);
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		Map<String, Object> resMap = new HashMap<String, Object>();
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime start = setting.getTimeType() == 0 ? setting.getStart() : TimeUtil.getTime(setting.getTimeType()).get("start");
		LocalDateTime end = setting.getTimeType() == 0 ? setting.getEnd() : TimeUtil.getTime(setting.getTimeType()).get("end");
		Map<String, Object> statisticsMap = iBigScreenSettingService.getZongHeGrade(start, end, userInfo, "", areaIdList, unitIdList);
		resMap.put("statistics", statisticsMap);
//		Map<String, Object> resMap = iBigScreenSettingService.getZongHe(setting.getStart(), setting.getEnd(), 1);
		Map<LocalDateTime, Map<String, Object>> trendMap = new LinkedHashMap<LocalDateTime, Map<String,Object>>();
		List<LocalDateTime> timeList = TimeUtil.getDateList(start, end);
		for(int i=0;i<timeList.size();i++) {
			if(i+1 == timeList.size()) break;
			Map<String, Object> resTrend = iBigScreenSettingService.getZongHeGrade(timeList.get(i), timeList.get(i+1), userInfo, "", areaIdList, unitIdList);
		    trendMap.put(timeList.get(i+1), resTrend);
		}
		resMap.put("trend", trendMap);
		Map<String, Object> rankingMap = iBigScreenSettingService.getZongHeDevice(start, end, userInfo, "", areaIdList, unitIdList);
		resMap.put("ranking", rankingMap);
		Map<String, Object> typeMap = iBigScreenSettingService.getZongHeType(start, end, userInfo, "", areaIdList, unitIdList);
		resMap.put("type", typeMap);
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
		return res;
	}
	
	@GetMapping("getZhuDong")
	public ResultObject getZhuDong(Integer bigscreenId, String areaIdList, String unitIdList) {
		ResultObject res = new ResultObject<>();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		BigScreenSetting setting = iBigScreenSettingService.getById(bigscreenId);
		LocalDateTime start = setting.getTimeType() == 0 ? setting.getStart() : TimeUtil.getTime(setting.getTimeType()).get("start");
		LocalDateTime end = setting.getTimeType() == 0 ? setting.getEnd() : TimeUtil.getTime(setting.getTimeType()).get("end");
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> statisticsMap = iBigScreenSettingService.getZhuDongIp(start, end, userInfo, setting.getTimeType(), areaIdList, unitIdList);
		resMap.put("statistics", statisticsMap);
		Map<LocalDateTime, Map<String, Object>> trendMap = new LinkedHashMap<LocalDateTime, Map<String,Object>>();
		List<LocalDateTime> timeList = TimeUtil.getDateList(start, end);
		for(int i=0;i<timeList.size();i++) {
			if(i+1 == timeList.size()) break;
			Map<String, Object> resTrend = iBigScreenSettingService.getZhuDong(timeList.get(i), timeList.get(i+1), userInfo, "grade", areaIdList, unitIdList);
		    trendMap.put(timeList.get(i+1), resTrend);
		}
		resMap.put("trend", trendMap);
		Map<String, Object> rankingMap = iBigScreenSettingService.getZhuDongRanking(start, end, userInfo, areaIdList, unitIdList);
		resMap.put("ranking", rankingMap);
		Map<String, Object> typeMap = iBigScreenSettingService.getZongHeType(start, end, userInfo, "types", areaIdList, unitIdList);
		resMap.put("type", typeMap);
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
		return res;
	}
	
	@GetMapping("getJingWai")
	public ResultObject getJingWai(Integer bigscreenId, String areaIdList, String unitIdList) {
		ResultObject res = new ResultObject<>();
		BigScreenSetting setting = iBigScreenSettingService.getById(bigscreenId);
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
//		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDateTime start = setting.getTimeType() == 0 ? setting.getStart() : TimeUtil.getTime(setting.getTimeType()).get("start");
		LocalDateTime end = setting.getTimeType() == 0 ? setting.getEnd() : TimeUtil.getTime(setting.getTimeType()).get("end");
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> statisticsMap = iBigScreenSettingService.getZongHeGrade(start, end, userInfo, "JingWai", areaIdList, unitIdList);
		resMap.put("statistics", statisticsMap);
//		Map<String, Object> resMap = iBigScreenSettingService.getZongHe(setting.getStart(), setting.getEnd(), 1);
		Map<LocalDateTime, Map<String, Object>> trendMap = new LinkedHashMap<LocalDateTime, Map<String,Object>>();
		List<LocalDateTime> timeList = TimeUtil.getDateList(start, end);
		for(int i=0;i<timeList.size();i++) {
			if(i+1 == timeList.size()) break;
			Map<String, Object> resTrend = iBigScreenSettingService.getZongHeGrade(timeList.get(i), timeList.get(i+1), userInfo, "JingWai", areaIdList, unitIdList);
		    trendMap.put(timeList.get(i+1), resTrend);
		}
		resMap.put("trend", trendMap);
		Map<String, Object> rankingMap = iBigScreenSettingService.getZongHeDevice(start, end, userInfo, "JingWai", areaIdList, unitIdList);
		resMap.put("ranking", rankingMap);
		Map<String, Object> typeMap = iBigScreenSettingService.getZongHeType(start, end, userInfo, "JingWai", areaIdList, unitIdList);
		resMap.put("type", typeMap);
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
		return res;
	}
	
	@GetMapping("detail")
	public ResultObject detail(Integer bigscreenId, String areaIdList, String unitIdList) {
		ResultObject res = new ResultObject<>();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		BigScreenSetting setting = iBigScreenSettingService.getById(bigscreenId);
		LocalDateTime start = setting.getTimeType() == 0 ? setting.getStart() : TimeUtil.getTime(setting.getTimeType()).get("start");
		LocalDateTime end = setting.getTimeType() == 0 ? setting.getEnd() : TimeUtil.getTime(setting.getTimeType()).get("end");
		try {
			List<Apt> resList = iBigScreenSettingService.detail(start, end, userInfo, areaIdList, unitIdList);
//			List<Apt> resList = iBigScreenSettingService.detail(setting.getStart(), setting.getEnd(), userInfo, areaIdList, unitIdList);
		    res.setData(resList);
		    res.setCode(HttpServletResponse.SC_OK);
		} catch (IOException e) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
			e.printStackTrace();
		} catch (ParseException e) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
			e.printStackTrace();
		}
		
		return res;
	}
}
