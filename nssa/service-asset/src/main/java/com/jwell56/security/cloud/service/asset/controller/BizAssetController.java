package com.jwell56.security.cloud.service.asset.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.asset.entity.AssetVo;
import com.jwell56.security.cloud.service.asset.entity.BizAsset;
import com.jwell56.security.cloud.service.asset.service.IBizAssetService;
import com.jwell56.security.cloud.service.asset.service.feign.NetStructComponent;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 资产表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-12-20
 */
@RestController
@RequestMapping("/asset")
public class BizAssetController {
    @Autowired
    private IBizAssetService iBizAssetService;

    @Autowired
    private NetStructComponent netStructComponent;

    @ApiOperation("根据IP、区域、单位，获取对应资产")
    @RequestMapping(value = "/getAsset", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "ip", value = "ip", dataType = "String"),
            @ApiImplicitParam(paramType = "query", name = "areaId", value = "区域id", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "unitId", value = "单位id", dataType = "Integer"),
    })
    public ResultObject<AssetVo> getIps(String ip, Integer areaId, Integer unitId) {
        try {
            BizAsset bizAsset = null;

            if (ip != null && !ip.isEmpty()) {
                QueryWrapper<BizAsset> bizAssetQueryWrapper = new QueryWrapper<>();
                bizAssetQueryWrapper.lambda().eq(BizAsset::getIp, ip);
                if (areaId != null && areaId != 0) {
                    List<Integer> areaIdList = netStructComponent.areaGetChildren(areaId);
                    bizAssetQueryWrapper.lambda().in(BizAsset::getAreaId, areaIdList);
                }
                if (unitId != null && unitId != 0) {
                    List<Integer> unitIdList = netStructComponent.unitGetChildren(unitId);
                    bizAssetQueryWrapper.lambda().in(BizAsset::getUnitId, unitIdList);
                }
                bizAsset = iBizAssetService.getOne(bizAssetQueryWrapper);
            }

            AssetVo assetVo = new AssetVo();
            if (bizAsset != null && bizAsset.getName() != null) {
                assetVo.setName(bizAsset.getName());
            } else {
                assetVo.setName("");
            }
            return ResultObject.data(assetVo);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
}
