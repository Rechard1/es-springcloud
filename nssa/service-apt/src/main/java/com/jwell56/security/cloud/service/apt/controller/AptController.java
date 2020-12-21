package com.jwell56.security.cloud.service.apt.controller;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jwell56.security.cloud.common.util.ExcelItem;
import com.jwell56.security.cloud.common.util.cache.ExcelUtils;
import com.jwell56.security.cloud.service.apt.entity.*;
import com.jwell56.security.cloud.service.apt.service.feign.AssetComponent;
import com.jwell56.security.cloud.service.apt.service.serviceImpl.ESAptService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.TimeUtil;
import com.jwell56.security.cloud.service.apt.service.IBigScreenSettingService;
import com.jwell56.security.cloud.service.apt.service.serviceImpl.AptServiceImpl;
import com.jwell56.security.cloud.service.apt.utils.ThreadLocalUtil;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "探针接口",tags = {"探针相关的controller"})
@Slf4j
@RequestMapping("/apt")
public class AptController {

    @Autowired
    private AptServiceImpl aptService;
    
	@Autowired
	private IBigScreenSettingService iBigScreenSettingService;
	
	@Autowired
    private ESAptService esAptService;

	@Autowired
    private AssetComponent assetComponent;
	
    @ApiOperation("攻击类型") //是查询数据库 还是统计ES    统计ES
    @RequestMapping(value = "/selectRiskType", method = RequestMethod.GET)
    public ResultObject selectRiskType(String file) {
        try {
            List<String> list =  aptService.selectRiskType(file);
            list.remove("");
            return ResultObject.data(list);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("入侵检测")
    @RequestMapping(value = "/selectApt", method = RequestMethod.POST)
    public ResultObject selectApt(@RequestBody ParamPI paramPI) {
        try {
            paramPI.setEnterpriseId(ThreadLocalUtil.getInstance().getUserInfo().getEnterpriseId());
            return ResultObject.data(aptService.selectApt(paramPI));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("入侵检测详情")
    @RequestMapping(value = "/selectAptDetail", method = RequestMethod.GET)
    public ResultObject selectAptDetail(String rowkey,String riskType) {
        try {
            return ResultObject.data(aptService.selectAptDetail(rowkey,riskType));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("入侵检测导出")
    @GetMapping("exprotExcel")
    public void exprotExcel(ParamPI paramPI,TimeParam timeParam,HttpServletResponse response,String type) throws IOException, ParseException {
        paramPI.setTimeParam(timeParam);
        paramPI.setEnterpriseId(ThreadLocalUtil.getInstance().getUserInfo().getEnterpriseId());
        paramPI.setPageNum(1);
        paramPI.setPageSize(1000);
        IPage<Apt> iPage = aptService.selectApt(paramPI);
        List<Apt> result = iPage.getRecords();

        String fileName = "入侵检测";
        response.setContentType("application/force-download");// 设置强制下载不打开
        response.setCharacterEncoding("utf-8");
        response.addHeader("Content-Disposition", "form-data;name=attachment;fileName=" + type +".xlsx");// 设置文件名
        OutputStream out = response.getOutputStream();
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);

        LinkedHashMap sysFiledSettingUser = assetComponent.getFiledSetting(type);

        //默认的
        List<List<String>> headList = new LinkedList<List<String>>();
        //取字段值值
        List<String> filed = new LinkedList<>();
        boolean sum = paramPI.isSum();
        if(sum == true){
            List<String> head = new LinkedList<>();
            head.add("聚合数据");
            headList.add(head);

            filed.add("counts");
        }

        String file = "";
        if(sysFiledSettingUser == null || (int)sysFiledSettingUser.get("selfAdaption")== 0){
            file = assetComponent.getAllFiledSettingDefalut(type);

        }else{
            file = (String) sysFiledSettingUser.get("filed");
        }
        LinkedHashMap<String,Map<String,JSONObject>> map = JSONObject.parseObject(file,LinkedHashMap.class);
        Set<String> mapSet = map.keySet();
        for(String key: mapSet) {
            Map<String, JSONObject> subMap = map.get(key);
            for(Map.Entry<String,JSONObject> entry: subMap.entrySet()){
                FiledVo filedVo = JSONObject.toJavaObject(entry.getValue(),FiledVo.class);
                if(filedVo.isValue()){
                    List<String> head = new LinkedList<>();
                    head.add(filedVo.getLabel());
                    headList.add(head);

                    filed.add(entry.getKey());
                }
            }
        }
        List<List<Object>> dataInfo = new LinkedList<List<Object>>();
        for(Apt apt : result){
            List<Object> object = new ArrayList<>();
            for(String str : filed){
                if(str.equals("counts")){
                    object.add(apt.getCounts());
                }else if(str.equals("happenTime")){
                    object.add(apt.getHappenTime().toString().replace("T"," "));
                }else if(str.equals("sip")){
                    object.add(apt.getSip());
                } else if(str.equals("smac")){
                    object.add(apt.getSmac());
                } else if(str.equals("sport")){
                    object.add(apt.getSport());
                } else if(str.equals("sAreaName")){
                    object.add(apt.getSAreaName());
                } else if(str.equals("sUnitName")){
                    object.add(apt.getSUnitName());
                } else if(str.equals("sAssetName")){
                    object.add(apt.getSAssetName());
                } else if(str.equals("sDomain")){
                    object.add(apt.getSDomain());
                } else if(str.equals("dip")){
                    object.add(apt.getDip());
                } else if(str.equals("dmac")){
                    object.add(apt.getDmac());
                } else if(str.equals("dport")){
                    object.add(apt.getDport());
                } else if(str.equals("dAreaName")){
                    object.add(apt.getDAreaName());
                } else if(str.equals("dUnitName")){
                    object.add(apt.getDUnitName());
                } else if(str.equals("dAssetName")){
                    object.add(apt.getDAssetName());
                }else if(str.equals("dDomain")){
                    object.add(apt.getDDomain());
                }else if(str.equals("riskType")){
                    object.add(apt.getRiskType());
                }else if(str.equals("riskGrade")){
                    object.add(apt.getRiskGrade());
                }else if(str.equals("deviceAreaName")){
                    object.add(apt.getDeviceAreaName());
                }else if(str.equals("deviceUnitName")){
                    object.add(apt.getDeviceUnitName());
                }else if(str.equals("deviceName")){
                    object.add(apt.getDeviceName());
                }

                dataInfo.add(object);
            }
        }
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName(fileName);
        sheet.setHead(headList);
        writer.write1(dataInfo, sheet);
        writer.finish();

        out.flush();
    }

    @ApiOperation(value = "入侵检测")
    @RequestMapping(value = {"/getEchars"}, method = RequestMethod.GET)
    public ResultObject getEchars() {
        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            Map<String,Map<String,Object>> map = new LinkedHashMap<>();
            List<String> list = new LinkedList<>();
            list.add("00:00:00");
            list.add("04:00:00");
            list.add("08:00:00");
            list.add("12:00:00");
            list.add("16:00:00");
            list.add("20:00:00");
            list.add("24:00:00");
            for(int i=0; i<list.size() ;i++){
            	if(i+1 == list.size()) break;
                Map<String,Object> mapApt = new LinkedHashMap<>();
//                if(list.get(i).equals("00:00:00")){
//                    mapApt.put("高",0);
//                    mapApt.put("中",0);
//                    mapApt.put("低",0);
//                    mapApt.put("total",0);
//                }else{
//                   
//                }
                String string = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
                LocalDateTime start = LocalDateTime.parse(string +" "+ list.get(i), df);
                LocalDateTime end = LocalDateTime.parse(string +" "+ list.get(i+1), df);
                
                mapApt = iBigScreenSettingService.getZongHeGrade(start, end, userInfo, "", "", "");
                map.put(list.get(i),mapApt);
            }

            resultObject.setData(map);
            resultObject.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return resultObject;
    }
    
    @GetMapping("/home/initiative")
    public ResultObject initiative() {
    	ResultObject res = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setGroupingUsed(false);
      	DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
      	Map<String, Object> resMap = new HashMap<String, Object>();
      	String string = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString() + " 00:00:00";
      	LocalDateTime start = LocalDateTime.parse(string, df);
      	LocalDateTime end = LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),df);
      	Map<String, Object> zhuDongMap = iBigScreenSettingService.getByIp(start, end, userInfo, "sip", "", "");
      	int total = iBigScreenSettingService.totalRows(start, end, userInfo, "", "");
      	Map<String, LocalDateTime> timeMap = TimeUtil.getTimeMap(4, start, end);
		if(timeMap.containsKey("lastStart") && timeMap.containsKey("lastEnd")) {
			LocalDateTime lastStart = timeMap.get("lastStart");
			LocalDateTime lastEnd = timeMap.get("lastEnd");
			int lastTotal = iBigScreenSettingService.totalRows(lastStart, lastEnd, userInfo, "", "");
			Map<String, Object> zhuDongMap1 = iBigScreenSettingService.getByIp(lastStart, lastEnd, userInfo, "sip", "", "");
//			long total = rowkeyMap.containsKey("rowkey") ? Long.parseLong(rowkeyMap.get("rowkey").toString()) : 0;
//			long lastTotal = rowkeyMap1.containsKey("rowkey") ? Long.parseLong(rowkeyMap1.get("rowkey").toString()) : 0;
			long zhuDong = zhuDongMap.containsKey("sip") ? Long.parseLong(zhuDongMap.get("sip").toString()) : 0;
			long lastZhuDong = zhuDongMap1.containsKey("sip") ? Long.parseLong(zhuDongMap1.get("sip").toString()) : 0;
			if(lastTotal == 0) {
				resMap.put("totalFlag", false);
				resMap.put("totalYoy", "--");
			}
			else if(total - lastTotal < 0) {
				resMap.put("totalFlag", false);
				resMap.put("totalYoy", nf.format(((lastTotal - total) * 100 / lastTotal)));
			}else {
				resMap.put("totalFlag", true);
				resMap.put("totalYoy", nf.format(((total - lastTotal) * 100 / lastTotal)));
			}
			if(lastZhuDong == 0) {
				resMap.put("zhuDongFlag", false);
				resMap.put("zhuDongYoy", "--");
			}
			else if(zhuDong - lastZhuDong < 0) {
				resMap.put("zhuDongFlag", false);
				resMap.put("zhuDongYoy", nf.format(((lastZhuDong - zhuDong) * 100 / lastZhuDong)));
			}else {
				resMap.put("zhuDongFlag", true);
				resMap.put("zhuDongYoy", nf.format(((zhuDong - lastZhuDong) * 100 / lastZhuDong)));
			}
			resMap.put("total", total);
			resMap.put("zhuDong", zhuDong);
		}
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
      	return res;
    }

    @GetMapping("/trend")
    public ResultObject aptTrendStatistic(
            @RequestParam("dataType") Integer dataType,
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {

        timeParam.setTimeType(timeType);
//        timeParam.setTimeType(6);

        ResultObject resultObject = new ResultObject();
        List<Map<String, Integer>> resultList = esAptService.aptTrendStatistic(dataType, areaIdList, unitIdList, deviceType, timeParam);
        resultObject.setData(resultList);
        return resultObject;
    }


    @GetMapping("/pie")
    public ResultObject aptGetPieStatistic(
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {

        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        List<Map<String, Object>> resultList = esAptService.getPie(areaIdList, unitIdList, deviceType, timeParam);
        resultObject.setData(resultList);
        return resultObject;
    }

    @GetMapping("/grade-pie")
    public ResultObject aptGradeStatistic(
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {
        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        List<Map<String, Object>> resultList = esAptService.getGradeList(areaIdList, unitIdList, deviceType, timeParam);
        resultObject.setData(resultList);
        return resultObject;
    }





    @GetMapping("/attacked-count")
    public ResultObject getAttackedCount(
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {
        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        Integer result = esAptService.getAttackedCount(areaIdList, unitIdList, "", timeParam, null);
        resultObject.setData(result);
        return resultObject;
    }

    @GetMapping("/attack-count")
    public ResultObject getAttackCount(
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {
        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        Integer result = esAptService.getAttackCount(areaIdList, unitIdList, "", timeParam, null);
        resultObject.setData(result);
        return resultObject;
    }


    @GetMapping("/description")
    public ResultObject getDesData(
            @RequestParam("dataType") Integer dataType,
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {
        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        String description = esAptService.getDesData(dataType, timeParam, areaIdList, unitIdList, deviceType);
        resultObject.setData(description);
        return resultObject;
    }

    @GetMapping("/top")
    public ResultObject getTopData(
            @RequestParam("dataType") Integer dataType,
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("n") Integer n,
            @RequestParam("deviceType") String deviceType,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {
        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        List<Map<String, Object>> pages = esAptService.getTop(dataType, areaIdList, unitIdList, n, deviceType, timeParam);
        resultObject.setData(pages);
        return resultObject;
    }


    @GetMapping("/risk-class-ip")
    public ResultObject getRiskClassTypeCount(
            @RequestParam("areaIdList") List<Integer> areaIdList,
            @RequestParam("unitIdList") List<Integer> unitIdList,
            @RequestParam("timeType") Integer timeType,
            TimeParam timeParam
    ) {
        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        JSONArray pages = esAptService.getRiskClassTypeCount(areaIdList, unitIdList, timeParam);
        resultObject.setData(JSONArray.toJSONString(pages));
        resultObject.setCode(200);
        return resultObject;
    }

    @GetMapping("/grade-sip-count")
    public ResultObject sipCountMap(@RequestParam("dataType") Integer dataType,
                                    @RequestParam("areaIdList") List<Integer> areaIdList,
                                    @RequestParam("unitIdList") List<Integer> unitIdList,
                                    @RequestParam("timeType") Integer timeType,
                                    TimeParam timeParam
    ) {

        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        Map<String, Integer> dataMap = esAptService.sipCountMap(dataType, areaIdList, unitIdList, timeParam);
        resultObject.setData(JSONArray.toJSONString(dataMap));
        resultObject.setCode(200);
        return resultObject;

    }

    @GetMapping("/history-counts")
    public ResultObject getHistoryCount(@RequestParam("dataType") Integer dataType,
                                        @RequestParam("areaIdList") List<Integer> areaIdList,
                                        @RequestParam("unitIdList") List<Integer> unitIdList,
                                        @RequestParam("timeType") Integer timeType,
                                        TimeParam timeParam){

        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        Integer historyCount = esAptService.getHistoryCount(dataType, areaIdList, unitIdList,
                timeParam.hisStart(), timeParam.hisEnd(), "", "");
        resultObject.setData(historyCount);
        resultObject.setCode(200);
        return resultObject;
    }



    @GetMapping("/unit-sip-count")
    public ResultObject getAptDataByUnit(@RequestParam("areaIdList") List<Integer> areaIdList,
                                        @RequestParam("unitIdList") List<Integer> unitIdList,
                                        @RequestParam("timeType") Integer timeType,
                                        TimeParam timeParam){

        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        List<Map<String, Object>> unitSipDataList = esAptService.getAptDataByUnit(areaIdList, unitIdList, timeParam);
        resultObject.setData(unitSipDataList);
        resultObject.setCode(200);
        return resultObject;
    }


    @GetMapping("/unit-sip-count-table")
    public ResultObject getAptTableDataByUnit(@RequestParam("areaIdList") List<Integer> areaIdList,
                                         @RequestParam("unitIdList") List<Integer> unitIdList,
                                         @RequestParam("timeType") Integer timeType,
                                         TimeParam timeParam){

        timeParam.setTimeType(timeType);
        ResultObject resultObject = new ResultObject();
        List<Map<String, Object>> unitSipDataList = esAptService.getAptTableDataByUnit(areaIdList, unitIdList, timeParam);
        resultObject.setData(unitSipDataList);
        resultObject.setCode(200);
        return resultObject;
    }

    @GetMapping("/excel")
    public ResultObject generateExcel(
            @RequestParam("areaIdList") String areaIdList,
            @RequestParam("unitIdList") String unitIdList,
            @RequestParam("enterpriseId") Integer enterpriseId,
            TimeParam timeParam,
            @RequestParam("timeType") Integer timeType,
            @RequestParam("type") String type,
            @RequestParam(value = "targetFileName", required = false) String targetFileName) throws IOException, ParseException {

        timeParam.setTimeType(timeType);
        ParamPI paramPI = new ParamPI();
        paramPI.setSAreaIdList(areaIdList);
        paramPI.setSUnitIdList(unitIdList);
        paramPI.setTimeParam(timeParam);
        paramPI.setEnterpriseId(enterpriseId);
        paramPI.setPageNum(1);
        paramPI.setPageSize(1000);
        IPage<Apt> iPage = aptService.selectApt(paramPI);
        List<Apt> result = iPage.getRecords();

//        String fileName = "入侵检测";
//        response.setContentType("application/force-download");// 设置强制下载不打开
//        response.setCharacterEncoding("utf-8");
//        response.addHeader("Content-Disposition", "form-data;name=attachment;fileName=" + type +".xlsx");// 设置文件名

//        LocalDateTime nowTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String excelTime = String.valueOf(LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli());
        OutputStream out = null;

        if(StringUtils.isNotEmpty(targetFileName)){
            out = new FileOutputStream("/home/report/" + targetFileName);
        }else {
            out = new FileOutputStream("/home/report/入侵检测" + excelTime + ".xlsx");
        }
        ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX);

//        LinkedHashMap sysFiledSettingUser = assetComponent.getFiledSetting(type);

        //默认的
        List<List<String>> headList = new LinkedList<List<String>>();
        //取字段值值
        List<String> filed = new LinkedList<>();
//        boolean sum = paramPI.isSum();
//        if(sum == true){
//            List<String> head = new LinkedList<>();
//            head.add("聚合数据");
//            headList.add(head);
//
//            filed.add("counts");
//        }

        String file = "";
//        if(sysFiledSettingUser == null || (int)sysFiledSettingUser.get("selfAdaption")== 0){
        file = assetComponent.getAllFiledSettingDefalut(type);

//        }else{
//            file = (String) sysFiledSettingUser.get("filed");
//        }
        LinkedHashMap<String, Map<String, JSONObject>> map = JSONObject.parseObject(file, LinkedHashMap.class);
        Set<String> mapSet = map.keySet();
        for (String key : mapSet) {
            Map<String, JSONObject> subMap = map.get(key);
            for (Map.Entry<String, JSONObject> entry : subMap.entrySet()) {
                FiledVo filedVo = JSONObject.toJavaObject(entry.getValue(), FiledVo.class);
                if (filedVo.isValue()) {
                    List<String> head = new LinkedList<>();
                    head.add(filedVo.getLabel());
                    headList.add(head);

                    filed.add(entry.getKey());
                }
            }
        }
        List<List<Object>> dataInfo = new LinkedList<List<Object>>();
        for (Apt apt : result) {
            List<Object> object = new ArrayList<>();
            for (String str : filed) {
                if (str.equals("counts")) {
                    object.add(apt.getCounts());
                } else if (str.equals("happenTime")) {
                    object.add(apt.getHappenTime().toString().replace("T", " "));
                } else if (str.equals("sip")) {
                    object.add(apt.getSip());
                } else if (str.equals("smac")) {
                    object.add(apt.getSmac());
                } else if (str.equals("sport")) {
                    object.add(apt.getSport());
                } else if (str.equals("sAreaName")) {
                    object.add(apt.getSAreaName());
                } else if (str.equals("sUnitName")) {
                    object.add(apt.getSUnitName());
                } else if (str.equals("sAssetName")) {
                    object.add(apt.getSAssetName());
                } else if (str.equals("sDomain")) {
                    object.add(apt.getSDomain());
                } else if (str.equals("dip")) {
                    object.add(apt.getDip());
                } else if (str.equals("dmac")) {
                    object.add(apt.getDmac());
                } else if (str.equals("dport")) {
                    object.add(apt.getDport());
                } else if (str.equals("dAreaName")) {
                    object.add(apt.getDAreaName());
                } else if (str.equals("dUnitName")) {
                    object.add(apt.getDUnitName());
                } else if (str.equals("dAssetName")) {
                    object.add(apt.getDAssetName());
                } else if (str.equals("dDomain")) {
                    object.add(apt.getDDomain());
                } else if (str.equals("riskType")) {
                    object.add(apt.getRiskType());
                } else if (str.equals("riskGrade")) {
                    object.add(apt.getRiskGrade());
                } else if (str.equals("deviceAreaName")) {
                    object.add(apt.getDeviceAreaName());
                } else if (str.equals("deviceUnitName")) {
                    object.add(apt.getDeviceUnitName());
                } else if (str.equals("deviceName")) {
                    object.add(apt.getDeviceName());
                }

                dataInfo.add(object);
            }
        }
        Sheet sheet = new Sheet(1, 0);
        sheet.setSheetName("入侵检测.xlsx");
        sheet.setHead(headList);
        writer.write1(dataInfo, sheet);
        writer.finish();

        out.flush();

        ResultObject resultObject = new ResultObject();
        resultObject.setCode(200);
        resultObject.setData("入侵检测" + excelTime + ".xlsx");

        if (StringUtils.isNotEmpty(targetFileName)) {
            resultObject.setData(targetFileName);
        }

        return resultObject;
    }







    @GetMapping("/test")
    public String testElasticSearch() {
        return esAptService.testElasticSearch();
    }
}
