package com.jwell56.security.cloud.service.netstruct.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.service.IAreaService;
import com.jwell56.security.cloud.service.netstruct.utils.RedisUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/set/area/")
public class AreaSettingController {

	@Autowired
    private IAreaService iAreaService;
    
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation(value = "区域设置-添加", notes = "区域设置-添加")
    @RequestMapping(value = {"/add"}, method = RequestMethod.POST)
    public ResultObject add(@RequestBody @ApiParam(name = "区域设置", value = "传入json格式", required = true) Area area) {
        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            if (area.getPid() == null) {
                area.setPid(0);
            }
            if (area.getName() == null || area.getName().isEmpty()) {
                resultObject.setMsg("区域名称为空");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else if (iAreaService.areaExist(area.getPid(), area.getName(),userInfo.getEnterpriseId())) {
                resultObject.setMsg("区域名称已存在");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else {
            	area.setUserId(userInfo.getUserId());
            	area.setEnterpriseId(userInfo.getEnterpriseId());
                if (iAreaService.save(area)) {
                	resultObject.setData(area.getAreaId());
                    resultObject.setMsg("添加成功");
                    resultObject.setCode(HttpServletResponse.SC_OK);
                } else {
                    resultObject.setMsg("添加失败");
                    resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
                }
            }

            //修改后清空缓存
            CommonCachePool.deleteAll();

        } catch (Exception e) {
            e.printStackTrace();
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }

    @ApiOperation(value = "区域设置-修改", notes = "区域设置-修改")
    @RequestMapping(value = {"/update"}, method = RequestMethod.POST)
    public ResultObject update(@RequestBody @ApiParam(name = "区域设置", value = "传入json格式", required = true) Area area) {
        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            if (area.getPid() == null) {
                area.setPid(0);
            }
            Area areaExit = iAreaService.getById(area.getAreaId());
            if (area.getName() == null || area.getName().isEmpty()) {
                resultObject.setMsg("区域名称为空");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else if (!areaExit.getName().equals(area.getName()) &&
                    iAreaService.areaExist(area.getPid(), area.getName(),userInfo.getEnterpriseId())) {
                resultObject.setMsg("区域名称已存在");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else {
            	redisUtil.del("areaName_" + area.getAreaId());
                if (iAreaService.updateById(area)) {
                    resultObject.setMsg("修改成功");
                    resultObject.setCode(HttpServletResponse.SC_OK);
                } else {
                    resultObject.setMsg("修改失败");
                    resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
                }
            }

            //修改后清空缓存
            CommonCachePool.deleteAll();

        } catch (Exception e) {
            e.printStackTrace();
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }

    @ApiOperation(value = "区域设置-删除", notes = "区域设置-删除")
    @RequestMapping(value = {"/delete"}, method = RequestMethod.GET)
    public ResultObject delete(Integer areaId) {
        ResultObject resultObject = new ResultObject();
        try {
            if (areaId == null || areaId.equals(0)) {
                resultObject.setMsg("参数错误");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else {
            	redisUtil.del("areaName_" + areaId);
                if (iAreaService.removeById(areaId)) {
                    resultObject.setMsg("删除成功");
                    resultObject.setCode(HttpServletResponse.SC_OK);
                    //修改后清空缓存
                    CommonCachePool.deleteAll();
                } else {
                    resultObject.setMsg("删除失败");
                    resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }
}
