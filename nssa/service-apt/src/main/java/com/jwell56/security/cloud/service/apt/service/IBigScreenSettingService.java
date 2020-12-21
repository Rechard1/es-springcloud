package com.jwell56.security.cloud.service.apt.service;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.apt.entity.Apt;
import com.jwell56.security.cloud.service.apt.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.apt.entity.User;

public interface IBigScreenSettingService extends IService<BigScreenSetting>{

	Map<String, Object> getZongHeGrade(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList);
	
	Map<String, Object> getZongHeDevice(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList);
	
	Map<String, Object> getZongHeType(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList);
	
	Map<String, Object> getZhuDongIp(LocalDateTime start, LocalDateTime end, User userInfo, Integer timeType, String areaIdList, String unitIdList);
	
	Map<String, Object> getZhuDong(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList);
	
	Map<String, Object> getByIp(LocalDateTime start, LocalDateTime end, User userInfo, String flag, String areaIdList, String unitIdList);
	
	Map<String, Object> getZhuDongRanking(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList);
	
    int totalRows(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList);
	
	List<Apt> detail(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList) throws IOException, ParseException;
}