package com.jwell56.security.cloud.service.apt.service.feign;

import com.jwell56.security.cloud.common.ResultObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("service-asset")
public interface IAssetService {

    @GetMapping("/asset/getAssetById")
    ResultObject getAssetById(@RequestParam("assetId") Integer assetId);

    @GetMapping("/asset/device-type")
    ResultObject getIpListByDeviceType(
            @RequestParam("deviceType") String deviceType
    );

    @GetMapping("/asset/ip")
    ResultObject getAssetByIp(
            @RequestParam("ip") String ip
    );

    @GetMapping("/device/ids")
    ResultObject getDeviceByIds(@RequestParam("deviceIdsStr") String deviceIdsStr);

    @GetMapping("/filedSetting/getFiledSetting")
    ResultObject getFiledSetting(@RequestParam("type") String type);

    @GetMapping("/filedSetting/getAllFiledSettingDefalut")
    ResultObject getAllFiledSettingDefalut(@RequestParam("type") String type);

    @GetMapping("/asset/searchByName")
    ResultObject searchByName(@RequestParam("assetName") String assetName);
}
