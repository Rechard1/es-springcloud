package com.jwell56.security.cloud.service.asset.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.entity.SysFiledSetting;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.entity.other.Alert;
import com.jwell56.security.cloud.service.asset.entity.other.FileInfo;
import com.jwell56.security.cloud.service.asset.entity.other.Flow;
import com.jwell56.security.cloud.service.asset.entity.vo.FiledVo;
import com.jwell56.security.cloud.service.asset.service.ISysFiledSettingService;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/filedSetting")
public class FiledSettingController {

    @Autowired
    private ISysFiledSettingService iSysFiledSettingService;

    @ApiOperation("查询字段设置")
    @GetMapping("getFiledSetting")
    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token")
    public ResultObject getFiledSetting(String type) {
        if (type != null && !"".equals(type)) {
            User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
            int userId = userInfo.getUserId();
            QueryWrapper<SysFiledSetting> sysFiledSettingQueryWrapper = new QueryWrapper<>();
            sysFiledSettingQueryWrapper.lambda().eq(SysFiledSetting::getUserId, userId);
            sysFiledSettingQueryWrapper.lambda().eq(SysFiledSetting::getType, type);
            SysFiledSetting sysFiledSettingUser = iSysFiledSettingService.getOne(sysFiledSettingQueryWrapper);
            if (sysFiledSettingUser == null) {
                SysFiledSetting sysFiledSettingDefalut = new SysFiledSetting();
                sysFiledSettingDefalut.setUserId(userId);
                sysFiledSettingDefalut.setSelfAdaption(0);
                sysFiledSettingDefalut.setPage(10);
                sysFiledSettingDefalut.setType(type);
                sysFiledSettingDefalut.setFiled(JSONObject.toJSONString(defalutFiled(type)));
                boolean def = iSysFiledSettingService.save(sysFiledSettingDefalut);
                if (def == true) {
                    return ResultObject.data(sysFiledSettingDefalut);
                }
            } else {
                return ResultObject.data(sysFiledSettingUser);
            }
        }
        return ResultObject.data(null);
    }

    @ApiOperation("全文检索查询默认字段设置")
    @GetMapping("getAllFiledSettingDefalut")
    public ResultObject getAllFiledSettingDefalut(String type) {
        return ResultObject.data(JSONObject.toJSONString(defalutFiled(type)));
    }

    //字段射手和返回必须映射一样
    private Map<String, Map<String, FiledVo>> defalutFiled(String type) {
        Map<String, Map<String, FiledVo>> map = new LinkedHashMap<>();
        //公共
        //第一行
        Map<String, FiledVo> filedMapTime = new LinkedHashMap<>();
        FiledVo filedVoTime = new FiledVo();
        filedVoTime.setLabel("时间");
        filedVoTime.setValue(true);
        filedMapTime.put("happenTime", filedVoTime);

        //第二行
        Map<String, FiledVo> filedMapS = new LinkedHashMap<>();
        FiledVo filedVoSip = new FiledVo();
        filedVoSip.setLabel("来源ip");
        filedVoSip.setValue(true);
        filedMapS.put("sip", filedVoSip);

        FiledVo filedVoSmac = new FiledVo();
        filedVoSmac.setLabel("来源mac");
        filedVoSmac.setValue(false);
        filedMapS.put("smac", filedVoSmac);

        FiledVo filedVoSPort = new FiledVo();
        filedVoSPort.setLabel("来源端口");
        filedVoSPort.setValue(false);
        filedMapS.put("sport", filedVoSPort);

        FiledVo filedVoSarea = new FiledVo();
        filedVoSarea.setLabel("来源区域");
        filedVoSarea.setValue(false);
        filedMapS.put("sAreaName", filedVoSarea);

        FiledVo filedVoSunit = new FiledVo();
        filedVoSunit.setLabel("来源单位");
        filedVoSunit.setValue(false);
        filedMapS.put("sUnitName", filedVoSunit);

        FiledVo filedVoSasset = new FiledVo();
        filedVoSasset.setLabel("来源设备");
        filedVoSasset.setValue(false);
        filedMapS.put("sAssetName", filedVoSasset);

        FiledVo filedVoSdomain = new FiledVo();
        filedVoSdomain.setLabel("来源地区");
        filedVoSdomain.setValue(false);
        filedMapS.put("sDomain", filedVoSdomain);

        //第三行
        Map<String, FiledVo> filedMapD = new LinkedHashMap<>();
        FiledVo filedVoDip = new FiledVo();
        filedVoDip.setLabel("目的ip");
        filedVoDip.setValue(true);
        filedMapD.put("dip", filedVoDip);

        FiledVo filedVoDmac = new FiledVo();
        filedVoDmac.setLabel("目的mac");
        filedVoDmac.setValue(false);
        filedMapD.put("dmac", filedVoDmac);

        FiledVo filedVoDPort = new FiledVo();
        filedVoDPort.setLabel("目的端口");
        filedVoDPort.setValue(false);
        filedMapD.put("dport", filedVoDPort);

        FiledVo filedVoDarea = new FiledVo();
        filedVoDarea.setLabel("目的区域");
        filedVoDarea.setValue(false);
        filedMapD.put("dAreaName", filedVoDarea);

        FiledVo filedVoDunit = new FiledVo();
        filedVoDunit.setLabel("目的单位");
        filedVoDunit.setValue(false);
        filedMapD.put("dUnitName", filedVoDunit);

        FiledVo filedVoDasset = new FiledVo();
        filedVoDasset.setLabel("目的设备");
        filedVoDasset.setValue(false);
        filedMapD.put("dAssetName", filedVoDasset);

        FiledVo filedVoDdomain = new FiledVo();
        filedVoDdomain.setLabel("目的地区");
        filedVoDdomain.setValue(false);
        filedMapD.put("dDomain", filedVoDdomain);

        //第四行
        Map<String, FiledVo> filedMapRes = new LinkedHashMap<>();
        FiledVo filedVoLogType = new FiledVo();
        filedVoLogType.setLabel("日志类型");
        filedVoLogType.setValue(true);
        filedMapRes.put("logtype", filedVoLogType);

        FiledVo filedVoSeverity = new FiledVo();
        filedVoSeverity.setLabel("级别");
        filedVoSeverity.setValue(true);
        filedMapRes.put("severity", filedVoSeverity);

        FiledVo filedVoOperationClass = new FiledVo();
        filedVoOperationClass.setLabel("操作类型");
        filedVoOperationClass.setValue(false);
        filedMapRes.put("operationClass", filedVoOperationClass);

        FiledVo filedVoOperationResult = new FiledVo();
        filedVoOperationResult.setLabel("操作结果");
        filedVoOperationResult.setValue(false);
        filedMapRes.put("operationResult", filedVoOperationResult);

        //第五行
        Map<String, FiledVo> filedMapAsset = new LinkedHashMap<>();
        FiledVo filedVoAssetArea = new FiledVo();
        filedVoAssetArea.setLabel("日志区域");
        filedVoAssetArea.setValue(true);
        filedMapAsset.put("assetAreaName", filedVoAssetArea);

        FiledVo filedVoAssetUnit = new FiledVo();
        filedVoAssetUnit.setLabel("日志单位");
        filedVoAssetUnit.setValue(true);
        filedMapAsset.put("assetUnitName", filedVoAssetUnit);

        FiledVo filedVoAssetName = new FiledVo();
        filedVoAssetName.setLabel("日志设备");
        filedVoAssetName.setValue(true);
        filedMapAsset.put("assetName", filedVoAssetName);

        FiledVo filedVoAssetIp = new FiledVo();
        filedVoAssetIp.setLabel("日志设备ip");
        filedVoAssetIp.setValue(false);
        filedMapAsset.put("assetip", filedVoAssetIp);

        //第四行
        Map<String, FiledVo> filedMapRisk = new LinkedHashMap<>();
        FiledVo filedVoRiskType = new FiledVo();
        filedVoRiskType.setLabel("攻击类型");
        filedVoRiskType.setValue(true);
        filedMapRisk.put("riskType", filedVoRiskType);

        FiledVo filedVoRiskGrade = new FiledVo();
        filedVoRiskGrade.setLabel("风险等级");
        filedVoRiskGrade.setValue(true);
        filedMapRisk.put("riskGrade", filedVoRiskGrade);

        FiledVo filedVoMethod = new FiledVo();
        filedVoMethod.setLabel("访问方法");
        filedVoMethod.setValue(true);

        FiledVo filedVoUrl = new FiledVo();
        filedVoUrl.setLabel("url");
        filedVoUrl.setValue(true);

        Map<String, FiledVo> filedMapResN = new LinkedHashMap<>();
        filedMapResN.put("logtype", filedVoLogType);
        filedMapResN.put("severity", filedVoSeverity);
        filedMapResN.put("method", filedVoMethod);
        filedMapResN.put("url", filedVoUrl);

        Map<String, FiledVo> filedMapDeviceN = new LinkedHashMap<>();
        FiledVo filedVoDeviceAreaN = new FiledVo();
        filedVoDeviceAreaN.setLabel("防护区域");
        filedVoDeviceAreaN.setValue(true);
        filedMapDeviceN.put("deviceAreaName", filedVoDeviceAreaN);

        FiledVo filedVoDeviceUnitN = new FiledVo();
        filedVoDeviceUnitN.setLabel("防护单位");
        filedVoDeviceUnitN.setValue(true);
        filedMapDeviceN.put("deviceUnitName", filedVoDeviceUnitN);

        FiledVo filedVoDeviceNameN = new FiledVo();
        filedVoDeviceNameN.setLabel("防护设备");
        filedVoDeviceNameN.setValue(true);
        filedMapDeviceN.put("deviceName", filedVoDeviceNameN);


        Map<String, FiledVo> filedMapAssetName = new LinkedHashMap<>();
        FiledVo filedVoAssetNameS = new FiledVo();
        filedVoAssetNameS.setLabel("日志设备");
        filedVoAssetNameS.setValue(true);
        filedMapAssetName.put("assetName", filedVoAssetNameS);

        FiledVo filedVoAssetIP = new FiledVo();
        filedVoAssetIP.setLabel("日志设备IP");
        filedVoAssetIP.setValue(true);
        filedMapAssetName.put("assetip", filedVoAssetIP);

        FiledVo filedVoAssetArea1 = new FiledVo();
        filedVoAssetArea1.setLabel("日志区域");
        filedVoAssetArea1.setValue(false);
        filedMapAssetName.put("assetAreaName", filedVoAssetArea1);

        FiledVo filedVoAssetUnit1 = new FiledVo();
        filedVoAssetUnit1.setLabel("日志单位");
        filedVoAssetUnit1.setValue(false);
        filedMapAssetName.put("assetUnitName", filedVoAssetUnit1);

        Map<String, FiledVo> filedMapQt = new LinkedHashMap<>();
        FiledVo filedVoLogType1 = new FiledVo();
        filedVoLogType1.setLabel("日志类型");
        filedVoLogType1.setValue(true);
        filedMapQt.put("logtype", filedVoLogType1);

        FiledVo filedVoSeverity1 = new FiledVo();
        filedVoSeverity1.setLabel("日志级别");
        filedVoSeverity1.setValue(true);
        filedMapQt.put("severity", filedVoSeverity1);

        FiledVo filedVoRowLog = new FiledVo();
        filedVoRowLog.setLabel("日志信息");
        filedVoRowLog.setValue(true);
        filedMapQt.put("info", filedVoRowLog);

        Map<String, FiledVo> filedMapRisk1 = new LinkedHashMap<>();
        FiledVo filedVoRiskGrade1 = new FiledVo();
        filedVoRiskGrade1.setLabel("风险等级");
        filedVoRiskGrade1.setValue(true);
        filedMapRisk1.put("riskGrade", filedVoRiskGrade1);

        FiledVo filedVoRiskType1 = new FiledVo();
        filedVoRiskType1.setLabel("风险类型");
        filedVoRiskType1.setValue(true);
        filedMapRisk1.put("riskType", filedVoRiskType1);

        FiledVo filedVoRiskDes = new FiledVo();
        filedVoRiskDes.setLabel("风险描述");
        filedVoRiskDes.setValue(false);
        filedMapRisk1.put("riskDes", filedVoRiskDes);

        Map<String, FiledVo> filedMapResN1 = new LinkedHashMap<>();
        FiledVo filedVoLogType11 = new FiledVo();
        filedVoLogType11.setLabel("日志类型");
        filedVoLogType11.setValue(false);

        FiledVo filedVoSeverity11 = new FiledVo();
        filedVoSeverity11.setLabel("级别");
        filedVoSeverity11.setValue(false);

        FiledVo filedVoMethod1 = new FiledVo();
        filedVoMethod1.setLabel("访问方法");
        filedVoMethod1.setValue(false);

        FiledVo filedVoUrl1 = new FiledVo();
        filedVoUrl1.setLabel("url");
        filedVoUrl1.setValue(true);

        filedMapResN1.put("logtype", filedVoLogType11);
        filedMapResN1.put("severity", filedVoSeverity11);
        filedMapResN1.put("method", filedVoMethod1);
        filedMapResN1.put("url", filedVoUrl1);

        Map<String, FiledVo> filedMapAssetName1 = new LinkedHashMap<>();

        FiledVo filedVoAssetArea11 = new FiledVo();
        filedVoAssetArea11.setLabel("日志区域");
        filedVoAssetArea11.setValue(true);
        filedMapAssetName1.put("assetAreaName", filedVoAssetArea11);

        FiledVo filedVoAssetUnit11 = new FiledVo();
        filedVoAssetUnit11.setLabel("日志单位");
        filedVoAssetUnit11.setValue(true);
        filedMapAssetName1.put("assetUnitName", filedVoAssetUnit11);

        FiledVo filedVoAssetNameS1 = new FiledVo();
        filedVoAssetNameS1.setLabel("日志设备");
        filedVoAssetNameS1.setValue(true);
        filedMapAssetName1.put("assetName", filedVoAssetNameS1);

        FiledVo filedVoAssetIP1 = new FiledVo();
        filedVoAssetIP1.setLabel("日志设备IP");
        filedVoAssetIP1.setValue(false);
        filedMapAssetName1.put("assetip", filedVoAssetIP1);

        switch (type) {
            case "idsAlert":
                initMap(map, Alert.class);
                break;
            case "idsFlow":
                initMap(map, Flow.class);
                break;
            case "idsFileInfo":
                initMap(map, FileInfo.class);
                break;
            case "syslogSystem":
                map.put("time", filedMapTime);
                map.put("asset", filedMapAssetName);
                map.put("log", filedMapQt);
                break;
            case "syslogSystemSec":
                map.put("time", filedMapTime);
                map.put("asset", filedMapAssetName);
                map.put("risk", filedMapRisk1);
                map.put("log", filedMapQt);
                break;
            case "syslogNetwork":
                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                //第四行
                map.put("resN", filedMapResN1);
                map.put("asset", filedMapAssetName1);
                break;
            case "syslogNetworkSec":
                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                map.put("resN", filedMapResN1);
                map.put("asset", filedMapAssetName1);
                map.put("risk", filedMapRisk1);
                break;
            case "syslogSql":
                map.put("time", filedMapTime);
                map.put("asset", filedMapAssetName);
                map.put("log", filedMapQt);
                break;
            case "syslogSqlSec":
                map.put("time", filedMapTime);
                map.put("asset", filedMapAssetName);
                map.put("risk", filedMapRisk1);
                map.put("log", filedMapQt);
                break;
            case "syslogExchange":
                map.put("time", filedMapTime);
                map.put("asset", filedMapAssetName);
                map.put("log", filedMapQt);
                break;
            case "syslogExchangeSec":
                map.put("time", filedMapTime);
                map.put("asset", filedMapAssetName);
                map.put("risk", filedMapRisk1);
                map.put("log", filedMapQt);
                break;
            case "apt":
                //第五行
                Map<String, FiledVo> filedMapDevice = new LinkedHashMap<>();
                FiledVo filedVoDeviceArea = new FiledVo();
                filedVoDeviceArea.setLabel("探针区域");
                filedVoDeviceArea.setValue(true);
                filedMapDevice.put("deviceAreaName", filedVoDeviceArea);

                FiledVo filedVoDeviceUnit = new FiledVo();
                filedVoDeviceUnit.setLabel("探针单位");
                filedVoDeviceUnit.setValue(true);
                filedMapDevice.put("deviceUnitName", filedVoDeviceUnit);

                FiledVo filedVoDeviceName = new FiledVo();
                filedVoDeviceName.setLabel("探针设备");
                filedVoDeviceName.setValue(true);
                filedMapDevice.put("deviceName", filedVoDeviceName);

                FiledVo filedVoRiskSort = new FiledVo();
                filedVoRiskSort.setLabel("风险分类");
                filedVoRiskSort.setValue(false);
                filedMapRisk.put("riskSort", filedVoRiskSort);

                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                map.put("risk", filedMapRisk);
                map.put("device", filedMapDevice);
                break;
            case "iac":
                //第五行
                Map<String, FiledVo> filedMapDevice1 = new LinkedHashMap<>();
                FiledVo filedVoDeviceArea1 = new FiledVo();
                filedVoDeviceArea1.setLabel("探针区域");
                filedVoDeviceArea1.setValue(true);
                filedMapDevice1.put("deviceAreaName", filedVoDeviceArea1);

                FiledVo filedVoDeviceUnit1 = new FiledVo();
                filedVoDeviceUnit1.setLabel("探针单位");
                filedVoDeviceUnit1.setValue(true);
                filedMapDevice1.put("deviceUnitName", filedVoDeviceUnit1);

                FiledVo filedVoDeviceName1 = new FiledVo();
                filedVoDeviceName1.setLabel("探针设备");
                filedVoDeviceName1.setValue(true);
                filedMapDevice1.put("deviceName", filedVoDeviceName1);

                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                map.put("risk", filedMapRisk);
                map.put("device", filedMapDevice1);
                break;
            case "guardNet":
                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);

                Map<String, FiledVo> filedMapH = new LinkedHashMap<>();
                FiledVo filedVoHttp = new FiledVo();
                filedVoHttp.setLabel("协议");
                filedVoHttp.setValue(true);
                filedMapH.put("http", filedVoHttp);

                FiledVo filedVoApplication = new FiledVo();
                filedVoApplication.setLabel("应用");
                filedVoApplication.setValue(true);
                filedMapH.put("application", filedVoApplication);

                FiledVo filedVoAction = new FiledVo();
                filedVoAction.setLabel("动作");
                filedVoAction.setValue(true);
                filedMapH.put("action", filedVoAction);

                FiledVo filedVoNetFlow = new FiledVo();
                filedVoNetFlow.setLabel("流量");
                filedVoNetFlow.setValue(false);
                filedMapH.put("netFlow", filedVoNetFlow);

                FiledVo filedVoSessionNum = new FiledVo();
                filedVoSessionNum.setLabel("会话数");
                filedVoSessionNum.setValue(false);
                filedMapH.put("sessionNum", filedVoSessionNum);

                FiledVo filedVoDataPackages = new FiledVo();
                filedVoDataPackages.setLabel("数据包");
                filedVoDataPackages.setValue(false);
                filedMapH.put("dataPackages", filedVoDataPackages);

                map.put("http", filedMapH);

                map.put("device", filedMapDeviceN);
                break;
            case "guardWaf":
                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                map.put("risk", filedMapRisk);
                Map<String, FiledVo> filedMapA = new LinkedHashMap<>();
                FiledVo filedVoActionA = new FiledVo();
                filedVoActionA.setLabel("动作");
                filedVoActionA.setValue(false);
                filedMapA.put("action", filedVoActionA);

                FiledVo filedVoUrlA = new FiledVo();
                filedVoUrlA.setLabel("URL");
                filedVoUrlA.setValue(false);
                filedMapA.put("url", filedVoUrlA);

                map.put("act", filedMapA);
                map.put("device", filedMapDeviceN);
                break;
            case "guardVirus":
                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                Map<String, FiledVo> filedMapVirus = new LinkedHashMap<>();
                FiledVo filedVoVirusName = new FiledVo();
                filedVoVirusName.setLabel("病毒名称");
                filedVoVirusName.setValue(true);
                filedMapVirus.put("virusName", filedVoVirusName);

                FiledVo filedVoVirusRiskGrade = new FiledVo();
                filedVoVirusRiskGrade.setLabel("风险等级");
                filedVoVirusRiskGrade.setValue(true);
                filedMapVirus.put("riskGrade", filedVoVirusRiskGrade);

                FiledVo filedVoVirusFile = new FiledVo();
                filedVoVirusFile.setLabel("感染文件");
                filedVoVirusFile.setValue(false);
                filedMapVirus.put("virusFile", filedVoVirusFile);

                FiledVo filedVoVirusFinger = new FiledVo();
                filedVoVirusFinger.setLabel("病毒指纹");
                filedVoVirusFinger.setValue(false);
                filedMapVirus.put("virusFinger", filedVoVirusFinger);

                map.put("virus", filedMapVirus);

                Map<String, FiledVo> filedMapAA = new LinkedHashMap<>();
                FiledVo filedVoActionAA = new FiledVo();
                filedVoActionAA.setLabel("动作");
                filedVoActionAA.setValue(false);
                filedMapAA.put("action", filedVoActionAA);
                map.put("act", filedMapAA);
                map.put("device", filedMapDeviceN);
                break;
            case "guardIps":
                map.put("time", filedMapTime);
                map.put("s", filedMapS);
                map.put("d", filedMapD);
                map.put("risk", filedMapRisk);
                Map<String, FiledVo> filedMapAAA = new LinkedHashMap<>();
                FiledVo filedVoActionAAA = new FiledVo();
                filedVoActionAAA.setLabel("动作");
                filedVoActionAAA.setValue(false);
                filedMapAAA.put("action", filedVoActionAAA);
                map.put("act", filedMapAAA);
                map.put("device", filedMapDeviceN);
                break;
            case "virus":
                map.put("time", filedMapTime);

                Map<String, FiledVo> filedMapIp = new LinkedHashMap<>();
                FiledVo filedVoIp = new FiledVo();
                filedVoIp.setLabel("ip");
                filedVoIp.setValue(true);
                filedMapIp.put("ip", filedVoIp);

                FiledVo filedVoMac = new FiledVo();
                filedVoMac.setLabel("MAC");
                filedVoMac.setValue(false);
                filedMapIp.put("mac", filedVoMac);

                map.put("ip", filedMapIp);

                Map<String, FiledVo> filedMapRiskV = new LinkedHashMap<>();
                FiledVo filedVoRiskTypeV = new FiledVo();
                filedVoRiskTypeV.setLabel("风险类型");
                filedVoRiskTypeV.setValue(true);
                filedMapRiskV.put("riskType", filedVoRiskTypeV);

                FiledVo filedVoRiskGradeV = new FiledVo();
                filedVoRiskGradeV.setLabel("风险等级");
                filedVoRiskGradeV.setValue(true);
                filedMapRiskV.put("riskGrade", filedVoRiskGradeV);

                FiledVo filedVoResult = new FiledVo();
                filedVoResult.setLabel("结果");
                filedVoResult.setValue(true);
                filedMapRiskV.put("result", filedVoResult);

                map.put("risk", filedMapRiskV);

                Map<String, FiledVo> filedMapArea = new LinkedHashMap<>();
                FiledVo filedVoArea = new FiledVo();
                filedVoArea.setLabel("区域");
                filedVoArea.setValue(true);
                filedMapArea.put("areaName", filedVoArea);

                FiledVo filedVoUnit = new FiledVo();
                filedVoUnit.setLabel("单位");
                filedVoUnit.setValue(true);
                filedMapArea.put("unitName", filedVoUnit);

                FiledVo filedVoAssetNameVirus = new FiledVo();
                filedVoAssetNameVirus.setLabel("资产名称");
                filedVoAssetNameVirus.setValue(false);
                filedMapArea.put("assetName", filedVoAssetNameVirus);

                FiledVo filedVoVirusNames = new FiledVo();
                filedVoVirusNames.setLabel("反病毒服务器");
                filedVoVirusNames.setValue(true);
                filedMapArea.put("deviceNames", filedVoVirusNames);

                map.put("name", filedMapArea);


                Map<String, FiledVo> filedMapAll = new LinkedHashMap<>();
                FiledVo filedVoComputer = new FiledVo();
                filedVoComputer.setLabel("计算机名");
                filedVoComputer.setValue(false);
                filedMapAll.put("deviceName", filedVoComputer);

                FiledVo filedVoVirusFiles = new FiledVo();
                filedVoVirusFiles.setLabel("感染文件");
                filedVoVirusFiles.setValue(false);
                filedMapAll.put("infectionFile", filedVoVirusFiles);

                FiledVo filedVoVirusSource = new FiledVo();
                filedVoVirusSource.setLabel("感染源");
                filedVoVirusSource.setValue(false);
                filedMapAll.put("infectionSource", filedVoVirusSource);

                FiledVo filedVoAccount = new FiledVo();
                filedVoAccount.setLabel("登录名");
                filedVoAccount.setValue(false);
                filedMapAll.put("loginName", filedVoAccount);

                FiledVo filedVoPlatform = new FiledVo();
                filedVoPlatform.setLabel("平台");
                filedVoPlatform.setValue(false);
                filedMapAll.put("sysName", filedVoPlatform);

                FiledVo filedVoScanType = new FiledVo();
                filedVoScanType.setLabel("扫描类型");
                filedVoScanType.setValue(false);
                filedMapAll.put("scanType", filedVoScanType);

                FiledVo filedVoFileType = new FiledVo();
                filedVoFileType.setLabel("文件路径");
                filedVoFileType.setValue(false);
                filedMapAll.put("infectionPath", filedVoFileType);

                map.put("all", filedMapAll);
                break;
        }
        return map;
    }

    private void initMap(Map<String, Map<String, FiledVo>> map, Class<?> clazz) {
        for (Field field : clazz.getFields()) {
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            if (apiModelProperty != null && !"".equals(apiModelProperty.notes())) {
                String key = apiModelProperty.notes();
                map.computeIfAbsent(key, k -> new LinkedHashMap<>());
                map.get(key).put(field.getName(), new FiledVo(apiModelProperty.value(), apiModelProperty.readOnly()));
            }
        }
    }

    @ApiOperation("更新字段设置")
    @PostMapping("updateFiledSetting")
    public ResultObject updateFiledSetting(@RequestBody SysFiledSetting sysFiledSetting) {
        ResultObject res = new ResultObject();
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        Integer userId = userInfo.getUserId();
        if (userId != null) {
            sysFiledSetting.setUserId(userId);
            iSysFiledSettingService.saveOrUpdate(sysFiledSetting);
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("更新字段设置成功");
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("更新字段设置失败");
        }
        return res;
    }
}
