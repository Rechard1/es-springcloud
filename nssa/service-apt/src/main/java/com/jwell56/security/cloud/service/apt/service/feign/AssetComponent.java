package com.jwell56.security.cloud.service.apt.service.feign;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.apt.entity.FiledVo;
import com.jwell56.security.cloud.service.apt.entity.SysFiledSetting;
import com.jwell56.security.cloud.service.apt.entity.bo.SysDeviceBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class AssetComponent {

    @Autowired
    private IAssetService iAssetService;


    public List<String> getIpListByDeviceType(String deviceType){

        ResultObject resultObject = iAssetService.getIpListByDeviceType(deviceType);

        List<String> resultList = (List<String>) resultObject.getData();

        return resultList;
    }

    public JSONObject getAssetByIp(String ip) {

        ResultObject resultObject = iAssetService.getAssetByIp(ip);

        JSONObject assetObj = JSONObject.parseObject((String) resultObject.getData());
        return assetObj;
    }
    
    public Map getAssetById(int assetId) {
        ResultObject resultObject = iAssetService.getAssetById(assetId);
        Map asset = (Map) resultObject.getData();
        return asset;
    }

    public List<SysDeviceBo> getDeviceByIds(String idsStr) {
        return (List<SysDeviceBo>) iAssetService.getDeviceByIds(idsStr).getData();
    }

    public LinkedHashMap getFiledSetting(String type) {
        return (LinkedHashMap) iAssetService.getFiledSetting(type).getData();
    }

    public String  getAllFiledSettingDefalut(String type) {
        return (String) iAssetService.getAllFiledSettingDefalut(type).getData();
    }

    public List<Integer> searchByName(String assetName) {
        List<Integer> assetList = new ArrayList<>();
        ResultObject assetResultObject = iAssetService.searchByName(assetName);
        List<Map> list  = (List<Map>) assetResultObject.getData();
        for(Map map : list){
            assetList.add((Integer) map.get("asset_id"));
        }
        return assetList;
    }

}
