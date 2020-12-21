package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.SysDevice;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.mapper.SysDeviceMapper;
import com.jwell56.security.cloud.service.asset.service.ISysDeviceService;
import com.jwell56.security.cloud.service.asset.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.asset.service.feign.RoleUnitComponent;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 设备状态表 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-04
 */
@Service
public class SysDeviceServiceImpl extends ServiceImpl<BaseMapper<SysDevice>, SysDevice> implements ISysDeviceService {

    @Autowired
    private RoleAreaComponent roleAreaComponent;

    @Autowired
    private RoleUnitComponent roleUnitComponent;

    @Override
    public QueryWrapper<SysDevice> queryWrapperForAreaUnit(QueryWrapper<SysDevice> assetQueryWrapper) {
        try {
            if (assetQueryWrapper == null) {
                assetQueryWrapper = new QueryWrapper<>();
            }
            User userInfo = ThreadLocalUtil.getInstance().getUserInfo();

            //权限控制
            List<Integer> roleAreaIdList = roleAreaComponent.roleAreaList(userInfo.getRoleId(), userInfo.getEnterpriseId());
            List<Integer> roleUnitIdList = roleUnitComponent.roleUnitList(userInfo.getRoleId(), userInfo.getEnterpriseId());
            //当无权限时,返回，不进行查询
            if (roleAreaIdList == null || roleAreaIdList.isEmpty() || roleUnitIdList == null || roleUnitIdList.isEmpty()) {
                assetQueryWrapper.apply("1<>1");
            } else {
                assetQueryWrapper.lambda().in(SysDevice::getAreaId, roleAreaIdList);
                assetQueryWrapper.lambda().in(SysDevice::getUnitId, roleUnitIdList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return assetQueryWrapper;
    }
}
