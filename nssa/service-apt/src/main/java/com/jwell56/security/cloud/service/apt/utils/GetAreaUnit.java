package com.jwell56.security.cloud.service.apt.utils;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.apt.entity.AreaUnit;
import com.jwell56.security.cloud.service.apt.entity.ParamPI;
import com.jwell56.security.cloud.service.apt.entity.SysUser;
import com.jwell56.security.cloud.service.apt.entity.User;
import com.jwell56.security.cloud.service.apt.service.feign.RoleAreaComponent;
import com.jwell56.security.cloud.service.apt.service.feign.RoleUnitComponent;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Component
public class GetAreaUnit {

    @Autowired
    private RoleAreaComponent roleAreaComponent;

    @Autowired
    private RoleUnitComponent roleUnitComponent;

    //区域单位权限查询
    public AreaUnit getAreaUnit(ParamPI paramPI){
        User user = ThreadLocalUtil.getInstance().getUserInfo();
        List<Integer> areaIdListRole = new ArrayList<>();
        List<Integer> unitIdListRole = new ArrayList<>();
        if (user != null && user.getEnterpriseId()!=0) {
            paramPI.setEnterpriseId(user.getEnterpriseId());
            areaIdListRole = roleAreaComponent.roleAreaList(user.getRoleId(),user.getEnterpriseId());
            unitIdListRole = roleUnitComponent.roleUnitList(user.getRoleId(),user.getEnterpriseId());
        }


        List<Integer> sAreaIdList = new ArrayList<>();
        List<Integer> sUnitIdList = new ArrayList<>();
        if(paramPI.getSAreaIdList() != null){
            Arrays.asList(paramPI.getSAreaIdList().split(",")).stream().forEach(x -> {
                sAreaIdList.add(Integer.parseInt(x));
            });
        }
        if(paramPI.getSUnitIdList() != null){
            Arrays.asList(paramPI.getSUnitIdList().split(",")).stream().forEach(x -> {
                sUnitIdList.add(Integer.parseInt(x));
            });
        }

        List<Integer> dAreaIdList = new ArrayList<>();
        List<Integer> dUnitIdList = new ArrayList<>();
        if(paramPI.getDAreaIdList() != null){
            Arrays.asList(paramPI.getDAreaIdList().split(",")).stream().forEach(x -> {
                dAreaIdList.add(Integer.parseInt(x));
            });
        }
        if(paramPI.getDUnitIdList() != null){
            Arrays.asList(paramPI.getDUnitIdList().split(",")).stream().forEach(x -> {
                dUnitIdList.add(Integer.parseInt(x));
            });
        }

        List<Integer> deviceAreaIdList = new ArrayList<>();
        List<Integer> deviceUnitIdList = new ArrayList<>();
        if(paramPI.getDeviceAreaIdList() != null){
            Arrays.asList(paramPI.getDeviceAreaIdList().split(",")).stream().forEach(x -> {
                deviceAreaIdList.add(Integer.parseInt(x));
            });
        }
        if(paramPI.getDeviceUnitIdList() != null){
            Arrays.asList(paramPI.getDeviceUnitIdList().split(",")).stream().forEach(x -> {
                deviceUnitIdList.add(Integer.parseInt(x));
            });
        }
        AreaUnit areaUnit = new AreaUnit();
        areaUnit.setAreaIdListRole(areaIdListRole);
        areaUnit.setUnitIdListRole(unitIdListRole);
        areaUnit.setSAreaIdList(sAreaIdList);
        areaUnit.setSUnitIdList(sUnitIdList);
        areaUnit.setDAreaIdList(dAreaIdList);
        areaUnit.setDUnitIdList(dUnitIdList);
        areaUnit.setDeviceAreaIdList(deviceAreaIdList);
        areaUnit.setDeviceUnitIdList(deviceUnitIdList);
        return  areaUnit;
    }
}
