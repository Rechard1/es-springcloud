package com.jwell56.security.cloud.service.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.entity.*;
import com.jwell56.security.cloud.service.asset.entity.commons.PageParam;
import com.jwell56.security.cloud.service.asset.entity.vo.SysDeviceVo;
import com.jwell56.security.cloud.service.asset.mapper.SysDeviceMonitorAreaMapper;
import com.jwell56.security.cloud.service.asset.mapper.SysDeviceMonitorUnitMapper;
import com.jwell56.security.cloud.service.asset.service.*;
import com.jwell56.security.cloud.service.asset.service.feign.NetStructComponent;
import com.jwell56.security.cloud.service.asset.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.asset.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.asset.utils.FileUploadUtils;
import com.jwell56.security.cloud.service.asset.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/device")
public class DeviceController {

    @Autowired
    private ISysDeviceService iSysDeviceService;

    @Autowired
    private IAssetDeviceService iAssetDeviceService;

    @Autowired
    private NetStructComponent netStructComponent;

    @Autowired
    private ISysDeviceMonitorUnitService iSysDeviceMonitorUnitService;

    @Autowired
    private ISysDeviceMonitorAreaService iSysDeviceMonitorAreaService;

    @Autowired
    private RoleAreaComponent roleAreaComponent;

    @Autowired
    private RoleUnitComponent roleUnitComponent;

    @Autowired
    private IAssetService assetService;

    @Autowired
    private IDeviceIacSettingService iDeviceIacSettingService;
    
    @Autowired
    private SysDeviceMonitorAreaMapper sysDeviceMonitorAreaMapper;
    
    @Autowired
    private SysDeviceMonitorUnitMapper sysDeviceMonitorUnitMapper;

    @ApiOperation("根据设备id,查询设备")
    @GetMapping("getDeviceById")
    public ResultObject getDeviceById(Integer deviceId) {
        return ResultObject.data(iSysDeviceService.getById(deviceId));
    }

    @ApiOperation("新增设备")
    @GetMapping("add")
    @Transactional
    public ResultObject add(SysDevice sysDevice,String area,String unit) {
        ResultObject res = new ResultObject<>();
        //获取用户信息
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();

        QueryWrapper<SysDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysDevice::getIp,sysDevice.getIp());
        queryWrapper.lambda().eq(SysDevice::getEnterpriseId,userInfo.getEnterpriseId());
        SysDevice sysDeviceTrue = iSysDeviceService.getOne(queryWrapper);

        if(sysDeviceTrue != null){
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg(sysDeviceTrue.getName()+sysDeviceTrue.getIp()+"设备重复");
        }else{
            sysDevice.setUserId(userInfo.getUserId());
            sysDevice.setEnterpriseId(userInfo.getEnterpriseId());
            sysDevice.setCreateTime(LocalDateTime.now());

            boolean b = iSysDeviceService.save(sysDevice);
            if (b) {
                if(area != null && !area.equals("")){
                    List<Integer> areaM= StringIdsUtil.listIds(area);
                    List<SysDeviceMonitorArea> list = new ArrayList<>();
                    for(Integer areaMM : areaM){
                        SysDeviceMonitorArea sysDeviceMonitorArea = new SysDeviceMonitorArea();
                        sysDeviceMonitorArea.setDeviceId(sysDevice.getDeviceId());
                        sysDeviceMonitorArea.setAreaId(areaMM);
                        list.add(sysDeviceMonitorArea);
                    }
                    if(!list.isEmpty()){
                        iSysDeviceMonitorAreaService.saveBatch(list);
                    }
                }
                if(unit != null && !unit.equals("")){
                    List<Integer> unitM= StringIdsUtil.listIds(unit);
                    List<SysDeviceMonitorUnit> list = new ArrayList<>();
                    for(Integer unitMM : unitM){
                        SysDeviceMonitorUnit sysDeviceMonitorUnit = new SysDeviceMonitorUnit();
                        sysDeviceMonitorUnit.setDeviceId(sysDevice.getDeviceId());
                        sysDeviceMonitorUnit.setUnitId(unitMM);
                        list.add(sysDeviceMonitorUnit);
                    }
                    if(!list.isEmpty()){
                        iSysDeviceMonitorUnitService.saveBatch(list);
                    }
                }
                res.setCode(HttpServletResponse.SC_OK);
                res.setSuccess(Boolean.TRUE);
                res.setMsg("新增设备成功");

            } else {
                res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setSuccess(Boolean.FALSE);
                res.setMsg("新增设备失败");
            }
        }
        return res;
    }

    @ApiOperation("删除设备")
    @GetMapping("delete")
    @Transactional
    public ResultObject deleteDevice(String deviceIds) {
        ResultObject res = new ResultObject();

        boolean b = iSysDeviceService.removeByIds(StringIdsUtil.listIds(deviceIds));
        if (b) {
            QueryWrapper<SysDeviceMonitorArea> areaQueryWrapper = new QueryWrapper<SysDeviceMonitorArea>();
            areaQueryWrapper.lambda().in(SysDeviceMonitorArea::getDeviceId,StringIdsUtil.listIds(deviceIds));
            iSysDeviceMonitorAreaService.remove(areaQueryWrapper);

            QueryWrapper<SysDeviceMonitorUnit> unitQueryWrapper = new QueryWrapper<SysDeviceMonitorUnit>();
            unitQueryWrapper.lambda().in(SysDeviceMonitorUnit::getDeviceId,StringIdsUtil.listIds(deviceIds));
            iSysDeviceMonitorUnitService.remove(unitQueryWrapper);

            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("删除设备成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除设备失败");
        }

        return res;
    }

    @ApiOperation("修改设备")
    @GetMapping("update")
    @Transactional
    public ResultObject update(SysDevice sysDevice,String area,String unit) {
        ResultObject res = new ResultObject();

        boolean b = iSysDeviceService.updateById(sysDevice);
        if (b) {
            QueryWrapper<SysDeviceMonitorArea> areaQueryWrapper = new QueryWrapper<SysDeviceMonitorArea>();
            areaQueryWrapper.lambda().eq(SysDeviceMonitorArea::getDeviceId,sysDevice.getDeviceId());
            iSysDeviceMonitorAreaService.remove(areaQueryWrapper);

            QueryWrapper<SysDeviceMonitorUnit> unitQueryWrapper = new QueryWrapper<SysDeviceMonitorUnit>();
            unitQueryWrapper.lambda().eq(SysDeviceMonitorUnit::getDeviceId,sysDevice.getDeviceId());
            iSysDeviceMonitorUnitService.remove(unitQueryWrapper);

            if(area != null && !area.equals("")){
                List<Integer> areaM= StringIdsUtil.listIds(area);
                List<SysDeviceMonitorArea> list = new ArrayList<>();
                for(Integer areaMM : areaM){
                    SysDeviceMonitorArea sysDeviceMonitorArea = new SysDeviceMonitorArea();
                    sysDeviceMonitorArea.setDeviceId(sysDevice.getDeviceId());
                    sysDeviceMonitorArea.setAreaId(areaMM);
                    list.add(sysDeviceMonitorArea);
                }
                if(!list.isEmpty()){
                    iSysDeviceMonitorAreaService.saveBatch(list);
                }
            }
            if(unit != null && !unit.equals("")){
                List<Integer> unitM= StringIdsUtil.listIds(unit);
                List<SysDeviceMonitorUnit> list = new ArrayList<>();
                for(Integer unitMM : unitM){
                    SysDeviceMonitorUnit sysDeviceMonitorUnit = new SysDeviceMonitorUnit();
                    sysDeviceMonitorUnit.setDeviceId(sysDevice.getDeviceId());
                    sysDeviceMonitorUnit.setUnitId(unitMM);
                    list.add(sysDeviceMonitorUnit);
                }
                if(!list.isEmpty()){
                    iSysDeviceMonitorUnitService.saveBatch(list);
                }
            }

            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("修改资产成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改资产失败");
        }
        return res;
    }

    @ApiOperation("设备")
    @GetMapping("all")
    public ResultObject all(String devicetype) {
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        ResultObject res = new ResultObject();
        QueryWrapper<SysDevice> deviceQueryWrapper = new QueryWrapper<SysDevice>();
        deviceQueryWrapper.lambda().eq(SysDevice::getEnterpriseId,user.getEnterpriseId());
        //权限处理
        deviceQueryWrapper = iSysDeviceService.queryWrapperForAreaUnit(deviceQueryWrapper);
        if(devicetype != null){
            deviceQueryWrapper.lambda().eq(SysDevice::getType,devicetype);
        }
        deviceQueryWrapper.lambda().orderByDesc(SysDevice :: getCreateTime);
        List<SysDevice> device = iSysDeviceService.list(deviceQueryWrapper);
        res.setData(device);
        res.setCode(HttpServletResponse.SC_OK);
        return res;
    }

    @ApiOperation("设备")
    @GetMapping("allSyslog")
    public ResultObject allSyslog(String devicetype) {
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        ResultObject res = new ResultObject();
        QueryWrapper<SysDevice> deviceQueryWrapper = new QueryWrapper<SysDevice>();
        deviceQueryWrapper.lambda().eq(SysDevice::getEnterpriseId,user.getEnterpriseId());
        //权限处理
//        deviceQueryWrapper = iSysDeviceService.queryWrapperForAreaUnit(deviceQueryWrapper);
        if(devicetype != null){
            deviceQueryWrapper.lambda().eq(SysDevice::getType,devicetype);
        }
        deviceQueryWrapper.lambda().orderByDesc(SysDevice :: getCreateTime);
        List<SysDevice> device = iSysDeviceService.list(deviceQueryWrapper);
        res.setData(device);
        res.setCode(HttpServletResponse.SC_OK);
        return res;
    }

    @ApiOperation("资产页面")
    @GetMapping("paging")
    public ResultObject paging(PageParam pageParam,String devicetype) {
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        ResultObject res = new ResultObject();
        QueryWrapper<SysDevice> deviceQueryWrapper = new QueryWrapper<SysDevice>();
        //权限处理
//        deviceQueryWrapper = iSysDeviceService.queryWrapperForAreaUnit(deviceQueryWrapper);
        deviceQueryWrapper.lambda().eq(SysDevice::getEnterpriseId,user.getEnterpriseId());
        deviceQueryWrapper.lambda().orderByDesc(SysDevice :: getCreateTime);
        if(devicetype != null){
            deviceQueryWrapper.lambda().eq(SysDevice::getType,devicetype);
        }
        IPage<SysDevice> device = iSysDeviceService.page(pageParam.iPage(), deviceQueryWrapper);
        List<SysDeviceVo> sysDeviceVoList =new ArrayList();
        for(SysDevice sysDevice : device.getRecords()){
            SysDeviceVo sysDeviceVo = new SysDeviceVo();
            BeanUtils.copyProperties(sysDevice,sysDeviceVo);
            sysDeviceVoList.add(sysDeviceVo);
        }
        for(SysDeviceVo sys : sysDeviceVoList){
            QueryWrapper<SysDeviceMonitorArea> areaQueryWrapper = new QueryWrapper<SysDeviceMonitorArea>();
            areaQueryWrapper.lambda().eq(SysDeviceMonitorArea::getDeviceId,sys.getDeviceId());
            List<SysDeviceMonitorArea> listArea = iSysDeviceMonitorAreaService.list(areaQueryWrapper);
            List listAreaR = new ArrayList();
            for(SysDeviceMonitorArea sysDeviceMonitorArea : listArea){
                listAreaR.add(sysDeviceMonitorArea.getAreaId());
            }
            sys.setArea(StringUtils.join(listAreaR,","));

            QueryWrapper<SysDeviceMonitorUnit> unitQueryWrapper = new QueryWrapper<SysDeviceMonitorUnit>();
            unitQueryWrapper.lambda().eq(SysDeviceMonitorUnit::getDeviceId,sys.getDeviceId());
            List<SysDeviceMonitorUnit> listUnit = iSysDeviceMonitorUnitService.list(unitQueryWrapper);
            List listUnitR = new ArrayList();
            for(SysDeviceMonitorUnit sysDeviceMonitorUnit : listUnit){
                listUnitR.add(sysDeviceMonitorUnit.getUnitId());
            }
            sys.setUnit(StringUtils.join(listUnitR,","));

            sys.setAreaName(netStructComponent.getAreaName(sys.getAreaId()));
            sys.setUnitName(netStructComponent.getUnitName(sys.getUnitId()));
        }
        IPage<SysDeviceVo> devicePage = new Page<SysDeviceVo>();
        BeanUtils.copyProperties(device, devicePage);
        devicePage.setRecords(sysDeviceVoList);
        res.setData(devicePage);
        res.setCode(HttpServletResponse.SC_OK);
        return res;
    }









    @ApiOperation("新增资产和设备关联")
    @PostMapping("addAssetDevice")
    public ResultObject addAssetDevice(@RequestBody AssetDevice assetDevice) {
        ResultObject res = new ResultObject<>();
        //获取用户信息
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        assetDevice.setUserId(userInfo.getUserId());
        assetDevice.setEnterpriseId(userInfo.getEnterpriseId());

        boolean b = iAssetDeviceService.save(assetDevice);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("新增资产和设备关联");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增资产和设备关联");
        }
        return res;
    }

    @ApiOperation("关联分页")
    @GetMapping("assetDevicePaging")
    public ResultObject paging(PageParam pageParam) {
        ResultObject res = new ResultObject();
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<AssetDevice> deviceQueryWrapper = new QueryWrapper<AssetDevice>();
        deviceQueryWrapper.lambda().eq(AssetDevice::getEnterpriseId,userInfo.getEnterpriseId());
//        deviceQueryWrapper.lambda().eq(AssetDevice::getUserId,userInfo.getUserId());
//        List<Integer> roleAreaIdList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
//        List<Integer> roleUnitIdList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
        deviceQueryWrapper.lambda().orderByDesc(AssetDevice :: getCreateTime);
        IPage<AssetDevice> page = iAssetDeviceService.page(pageParam.iPage(),deviceQueryWrapper);

        IPage<AssetDeviceVo> devicePage = new Page<>();
        BeanUtils.copyProperties(page, devicePage);
        List<AssetDeviceVo> list = new ArrayList();

        for(AssetDevice assetDevice : page.getRecords()){
            AssetDeviceVo assetDeviceVo = new AssetDeviceVo();
            BeanUtils.copyProperties(assetDevice,assetDeviceVo);
            Asset asset = assetService.getById(assetDeviceVo.getAssetId());
            assetDeviceVo.setAssetName(asset.getName());
            assetDeviceVo.setAreaName(netStructComponent.getAreaName(asset.getAreaId()));
            assetDeviceVo.setUnitName(netStructComponent.getUnitName(asset.getUnitId()));
            list.add(assetDeviceVo);
        }
        devicePage.setRecords(list);
        res.setData(devicePage);
        res.setCode(HttpServletResponse.SC_OK);
        return res;
    }

    @ApiOperation("删除关联关系")
    @GetMapping("deleteAssetDevice")
    public ResultObject deleteAssetDevice(int assetDeviceId) {
        ResultObject res = new ResultObject();

        boolean b = iAssetDeviceService.removeById(assetDeviceId);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("删除关联关系成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除关联关系失败");
        }

        return res;
    }

    @ApiOperation("修改设备")
    @GetMapping("updateAssetDevice")
    public ResultObject updateAssetDevice(AssetDevice assetDevice) {
//        AssetDevice assetDeviceN = new AssetDevice();
//        BeanUtils.copyProperties(assetDevice,assetDeviceN);
//
//        UpdateWrapper updateWrapper = new UpdateWrapper();
//
//        iAssetDeviceService.update(assetDevice,updateWrapper);
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        assetDevice.setUserId(userInfo.getUserId());
        ResultObject res = new ResultObject();
        boolean b = iAssetDeviceService.updateById(assetDevice);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("修改资产成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改资产失败");
        }
        return res;
    }
    @GetMapping("list")
    public ResultObject list() {
        ResultObject res = new ResultObject();
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<SysDevice> wrapper = new QueryWrapper<SysDevice>();
        wrapper.lambda().eq(SysDevice::getEnterpriseId, userInfo.getEnterpriseId());
        wrapper.lambda().eq(SysDevice::getType, "防护设备");
        wrapper.lambda().orderByDesc(SysDevice::getCreateTime);
        List<SysDevice> list = iSysDeviceService.list(wrapper);

        res.setData(list);
        res.setCode(HttpServletResponse.SC_OK);
        return res;
    }

    @ApiOperation("查询设备")
    @GetMapping("selectDeviceIacSetting")
    public ResultObject selectDeviceIacSetting(Integer deviceId) {
        ResultObject res = new ResultObject();
        QueryWrapper<DeviceIacSetting> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(DeviceIacSetting::getDeviceId, deviceId);
        DeviceIacSetting deviceIacSetting = iDeviceIacSettingService.getOne(queryWrapper);

        res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
        res.setData(deviceIacSetting);
        res.setMsg("查询成功");
        return res;
    }

    @Transactional
    @ApiOperation("全局配置")
    @RequestMapping(value = "/updateDeviceIacSettingAll", method = RequestMethod.POST)
    public ResultObject updateDeviceIacSettingAll(DeviceIacSetting deviceIacSetting,
                                                  @RequestParam(name = "iacfile", required = false) MultipartFile iacfile[]) {

        ResultObject res = new ResultObject();
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        String path = "";
        for(MultipartFile multipartFile : iacfile){
            String filePath = FileUploadUtils.upload(multipartFile);
            if(path.equals("")){
                path = filePath;
            }else{
                path = path +";"+ filePath;
            }
        }
        QueryWrapper<DeviceIacSetting> queryWrapper = new QueryWrapper();
        queryWrapper.lambda().eq(DeviceIacSetting::getEnterpriseId,userInfo.getEnterpriseId());
        deviceIacSetting.setFile(path);
        boolean b = false;
        b = iDeviceIacSettingService.update(deviceIacSetting, queryWrapper);

        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("修改成功");
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改失败");
        }
        return res;
    }

    @Transactional
    @ApiOperation("修改配置")
    @RequestMapping(value = "/updateDeviceIacSetting", method = RequestMethod.POST)
    public ResultObject updateDeviceIacSetting(DeviceIacSetting deviceIacSetting) {

        ResultObject res = new ResultObject();
        boolean b = false;
        b = iDeviceIacSettingService.saveOrUpdate(deviceIacSetting);

        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("修改成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改失败");
        }
        return res;
    }

    @ApiOperation("上传文件")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultObject upload(int deviceIacSettingId,
                               @RequestParam(name = "iacfile", required = false) MultipartFile iacfile) {

        ResultObject res = new ResultObject();
        String filePath = FileUploadUtils.upload(iacfile);

        boolean b = false;
        DeviceIacSetting deviceIacSetting = iDeviceIacSettingService.getById(deviceIacSettingId);
        String file = deviceIacSetting.getFile();

        if(file == null || "".equals(file)){
            file = filePath;
        }else{
            if(file.endsWith(",")){
                file = file + filePath;
            }else{
                file = file + ";" + filePath;
            }
        }

        DeviceIacSetting deviceIacSettingNew = new DeviceIacSetting();
        deviceIacSettingNew.setDeviceIacSettingId(deviceIacSettingId);
        deviceIacSettingNew.setFile(file);
        b = iDeviceIacSettingService.saveOrUpdate(deviceIacSettingNew);

        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setData(file);
            res.setMsg("修改成功");
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改失败");
        }
        return res;
    }

    @GetMapping("/deleteFile")
    public ResultObject deleteFile(int deviceIacSettingId,String path) {
        ResultObject res = new ResultObject();
        File file = new File(path);
        if (file.exists()) {
            if (file.delete()) {
                DeviceIacSetting deviceIacSetting = iDeviceIacSettingService.getById(deviceIacSettingId);
                String files = deviceIacSetting.getFile();
                if(files.contains(path)){
                    if(files.contains(path+";")){
                        files = files.replace(path+";","");
                    }else{
                        files = files.replace(path,"");
                    }
                }

                DeviceIacSetting deviceIacSettingNew = new DeviceIacSetting();
                deviceIacSettingNew.setDeviceIacSettingId(deviceIacSettingId);
                deviceIacSettingNew.setFile(files);
                iDeviceIacSettingService.saveOrUpdate(deviceIacSettingNew);

                res.setCode(HttpServletResponse.SC_OK);
                res.setSuccess(Boolean.TRUE);
                res.setMsg("删除成功");
            } else {
                res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setSuccess(Boolean.FALSE);
                res.setMsg("删除失败");
            }
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("文件不存在！");
        }
        return res;
    }

    @ApiOperation(value = "下载文件", notes = "下载文件")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public String download(String filename,String filepath,HttpServletResponse response) throws Exception {

        try {
            if (filename != null) {
                File file = new File(filepath);
                // 如果文件存在，则进行下载
                if (file.exists()) {
                    FileUploadUtils.downloadFile(filename,filepath,response);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @GetMapping("/ids")
    public ResultObject getDeviceByIds(@RequestParam("deviceIdsStr") String deviceIdsStr) {
        ResultObject resultObject = new ResultObject();
        if (StringUtils.isNotEmpty(deviceIdsStr) && deviceIdsStr.length() != 0) {
            String[] idsStrArray = deviceIdsStr.split(",");
            List<Integer> ids = new ArrayList<>();
            for (String idStr : idsStrArray) {
                ids.add(Integer.parseInt(idStr));
            }
            QueryWrapper<SysDevice> sysDeviceQueryWrapper = new QueryWrapper<>();
            sysDeviceQueryWrapper.lambda().in(SysDevice::getDeviceId, ids);
            List<SysDevice> devices = iSysDeviceService.list(sysDeviceQueryWrapper);
            resultObject.setData(devices);

        }
        resultObject.setCode(HttpServletResponse.SC_OK);
        return resultObject;
    }
    
    @GetMapping("/getMonitor")
    public ResultObject getMonitor(Integer probeId) {
    	ResultObject res = new ResultObject();
    	List<Integer> areaList = sysDeviceMonitorAreaMapper.getAreaIdList(probeId);
    	List<Integer> unitList = sysDeviceMonitorUnitMapper.getUnitIdList(probeId);
    	Map<String, Object> resMap = new HashMap<String, Object>();
    	resMap.put("unit", StringIdsUtil.StringIds(unitList));
    	resMap.put("area", StringIdsUtil.StringIds(areaList));
    	
    	res.setCode(HttpServletResponse.SC_OK);
    	res.setData(resMap);
    	return res;
    }
}
