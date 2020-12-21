package com.jwell56.security.cloud.service.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.entity.*;
import com.jwell56.security.cloud.service.asset.entity.commons.PageParam;
import com.jwell56.security.cloud.service.asset.entity.vo.ModelRepositoryVo;
import com.jwell56.security.cloud.service.asset.service.IDeviceSnmpservice;
import com.jwell56.security.cloud.service.asset.service.IHomeSettingService;
import com.jwell56.security.cloud.service.asset.service.IModelRepositoryService;
import com.jwell56.security.cloud.service.asset.service.ISysDeviceService;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/home")
public class HomeSettingController {
    @Autowired
    private IHomeSettingService iHomeSettingService;

    @Autowired
    private ISysDeviceService iSysDeviceService;

    @Autowired
    private IDeviceSnmpservice iDeviceSnmpservice;

    @ApiOperation("查询配置")
    @RequestMapping(value = {"/getSetting"}, method = RequestMethod.GET)
    public ResultObject getSetting() {
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        HomeSetting homeSetting = iHomeSettingService.getById(user.getUserId());
        if(homeSetting == null){
            homeSetting = new HomeSetting();
            homeSetting.setUserId(user.getUserId());
            homeSetting.setModule("0");
            iHomeSettingService.save(homeSetting);
        }
        return ResultObject.data(homeSetting);
    }

    @ApiOperation("查询权限默认配置")
    @RequestMapping(value = {"/getRoleDefaultSetting"}, method = RequestMethod.GET)
    public ResultObject getRoleDefaultSetting() {
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        String module = "";
        if(user.getRoleType() == 1){
            module = "2,3,4,5";
        }
        if(user.getRoleType() == 2){
            module = "2,3,4,5";
        }
        if(user.getRoleType() == 3){
            module = "1,6";
        }
        return ResultObject.data(module);
    }

    @ApiOperation("修改配置")
    @RequestMapping(value = {"/updateSetting"}, method = RequestMethod.GET)
    public ResultObject updateSetting(String module) {
        ResultObject res = new ResultObject();

        HomeSetting homeSetting = new HomeSetting();
        homeSetting.setModule(module);
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        homeSetting.setUserId(user.getUserId());
        boolean b = iHomeSettingService.updateById(homeSetting);
        if (b) {
            res.setData(module);
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("修改配置成功");
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改配置失败");
        }
        return res;
    }

    @ApiOperation("探针信息")
    @RequestMapping(value = {"/getDevice"}, method = RequestMethod.GET)
    public ResultObject getDevice() {
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        int enterpriseId = userInfo.getEnterpriseId();
        QueryWrapper<SysDevice> deviceQueryWrapper = new QueryWrapper();
        deviceQueryWrapper.lambda().eq(SysDevice::getEnterpriseId,enterpriseId);
        deviceQueryWrapper.select("type,count(*) as counts ,sum(today_count) as sum");
        deviceQueryWrapper.groupBy("type");
        List<Map<String, Object>> mapObj = iSysDeviceService.listMaps(deviceQueryWrapper);

        Map<String,HomeDevice> mapResult = new HashMap<>();
        for (Map<String, Object> map : mapObj) {
            String type = (String) map.get("type");
            if(type.equals("安全探针")||type.equals("防护设备")||
                    type.equals("日志审计")|| type.equals("反病毒")||type.equals("工控安全")){

                HomeDevice homeDevice = new HomeDevice();
                homeDevice.setCounts(Integer.parseInt(map.get("counts").toString()));
                homeDevice.setSum((int) Float.parseFloat(map.get("sum").toString()));


                int devicez,devicel;
                QueryWrapper<SysDevice> deviceQueryWrapper1 = new QueryWrapper();
                deviceQueryWrapper1.lambda().eq(SysDevice::getEnterpriseId,enterpriseId);
                deviceQueryWrapper1.lambda().eq(SysDevice::getStatus,"在线");
                deviceQueryWrapper1.lambda().eq(SysDevice::getType,type);
                devicez = iSysDeviceService.count(deviceQueryWrapper1);

                QueryWrapper<SysDevice> deviceQueryWrapper2 = new QueryWrapper();
                deviceQueryWrapper2.lambda().eq(SysDevice::getEnterpriseId,enterpriseId);
                deviceQueryWrapper2.lambda().eq(SysDevice::getStatus,"离线");
                deviceQueryWrapper2.lambda().eq(SysDevice::getType,type);
                devicel = iSysDeviceService.count(deviceQueryWrapper2);

                homeDevice.setDevicez(devicez);
                homeDevice.setDevicel(devicel);

                mapResult.put(type,homeDevice);
            }
        }
        int snmpcount,snmpz,snmpl;
        QueryWrapper<DeviceSnmp> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(DeviceSnmp::getEnterpriseId,enterpriseId);
        queryWrapper.lambda().eq(DeviceSnmp::getState,0);
        snmpl = iDeviceSnmpservice.count(queryWrapper);

        QueryWrapper<DeviceSnmp> queryWrapper1 = new QueryWrapper();
        queryWrapper1.lambda().eq(DeviceSnmp::getEnterpriseId,enterpriseId);
        queryWrapper1.lambda().eq(DeviceSnmp::getState,1);
        snmpz = iDeviceSnmpservice.count(queryWrapper1);

        QueryWrapper<DeviceSnmp> queryWrapper2 = new QueryWrapper();
        queryWrapper2.lambda().eq(DeviceSnmp::getEnterpriseId,enterpriseId);
        snmpcount = iDeviceSnmpservice.count(queryWrapper2);

        HomeDevice homeDevice = new HomeDevice();
        homeDevice.setCounts(snmpcount);
        homeDevice.setSum(0);
        homeDevice.setDevicez(snmpz);
        homeDevice.setDevicel(snmpl);
        mapResult.put("运维监控",homeDevice);


        return ResultObject.data(mapResult);
    }


}
