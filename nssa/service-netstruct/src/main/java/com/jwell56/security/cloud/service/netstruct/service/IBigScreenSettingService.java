package com.jwell56.security.cloud.service.netstruct.service;

import java.time.LocalDateTime;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.netstruct.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.netstruct.entity.User;

public interface IBigScreenSettingService extends IService<BigScreenSetting>{

	Map<String, Object> detail(LocalDateTime start, LocalDateTime end, User userInfo, String areaIdList, String unitIdList);
}
