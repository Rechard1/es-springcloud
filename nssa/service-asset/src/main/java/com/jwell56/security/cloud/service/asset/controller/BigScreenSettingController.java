package com.jwell56.security.cloud.service.asset.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.entity.BigScreenSetting;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.service.IBigScreenSettingService;
import com.jwell56.security.cloud.service.asset.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value = "大屏模板接口",tags = {"大屏模板相关的controller"})
@RestController
@RequestMapping("/bigscreen/setting")
public class BigScreenSettingController {

	@Autowired
	private IBigScreenSettingService bigScreenService;
	
	@ApiOperation("添加大屏模板")
	@PostMapping("add")
	public ResultObject add(@RequestBody BigScreenSetting[] bigScreenSettings) {
		ResultObject res = new ResultObject<>();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		for(BigScreenSetting bigScreenSetting : bigScreenSettings) {
			if(bigScreenSetting.getBigscreenId() != null) {
				bigScreenService.updateById(bigScreenSetting);
				continue;
			}
			bigScreenSetting.setUserId(userInfo.getUserId());
			bigScreenSetting.setEnterpriseId(userInfo.getEnterpriseId());
			bigScreenService.save(bigScreenSetting);
//			if(bigScreenSetting.getTimeType() == 2) {
//				bigScreenSetting.setEnd(LocalDateTime.now());
//				bigScreenSetting.setStart(bigScreenSetting.getEnd().minusHours(1));
//			}
//			if(bigScreenSetting.getTimeType() == 4) {
//				bigScreenSetting.setEnd(LocalDateTime.now());
//				bigScreenSetting.setStart(bigScreenSetting.getEnd().minusDays(1));
//			}
//			if(bigScreenSetting.getTimeType() == 5) {
//				bigScreenSetting.setEnd(LocalDateTime.now());
//				bigScreenSetting.setStart(bigScreenSetting.getEnd().minusWeeks(1));
//			}
//			if(bigScreenSetting.getTimeType() == 6) {
//				bigScreenSetting.setEnd(LocalDateTime.now());
//				bigScreenSetting.setStart(bigScreenSetting.getEnd().minusMonths(1));
//			}
		}
//		boolean b = bigScreenService.saveBatch(Arrays.asList(bigScreenSettings));
//        if (b) {
//        	res.setCode(HttpServletResponse.SC_OK);
//        	res.setSuccess(Boolean.TRUE);
//        	res.setMsg("新增大屏模板成功");
//
//        } else {
//        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//            res.setSuccess(Boolean.FALSE);
//            res.setMsg("新增大屏模板失败");
//        }	
        res.setCode(HttpServletResponse.SC_OK);
    	res.setSuccess(Boolean.TRUE);
    	res.setMsg("新增大屏模板成功");
		return res;
	}
	
	@ApiOperation("删除大屏模板")
	@DeleteMapping("delete")
	public ResultObject delete(String settingId) {
		ResultObject res = new ResultObject();
		
		boolean b = bigScreenService.removeByIds(StringIdsUtil.listIds(settingId));
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除大屏模板成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除大屏模板失败");
        }
		
		return res;
	}
	
	@ApiOperation("大屏模板详情页面")
	@GetMapping("detail")
	public ResultObject detail(Integer settingId) {
		ResultObject res = new ResultObject();
		BigScreenSetting bigScreen = bigScreenService.getById(settingId);
		res.setData(bigScreen);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
	
	@ApiOperation("大屏模板列表")
	@GetMapping("list")
	public ResultObject list() {
		ResultObject res = new ResultObject<>();
		//获取用户信息
		User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
		QueryWrapper<BigScreenSetting> queryWrapper = new QueryWrapper<BigScreenSetting>();
//		if(userInfo.getRoleType() == 1) {
//			queryWrapper.lambda().eq(BigScreenSetting :: getEnterpriseId, userInfo.getEnterpriseFlag());
//		}else {
//			queryWrapper.lambda().eq(BigScreenSetting :: getEnterpriseId, userInfo.getEnterpriseId());
//		}
		queryWrapper.lambda().eq(BigScreenSetting :: getEnterpriseId, userInfo.getEnterpriseId());
		List<BigScreenSetting> bigScreenList = bigScreenService.list(queryWrapper);
		res.setData(bigScreenList);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	} 
}
