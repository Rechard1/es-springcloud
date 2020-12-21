package com.jwell56.security.cloud.service.netstruct.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.netstruct.entity.Unit;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.service.IUnitService;
import com.jwell56.security.cloud.service.netstruct.utils.RedisUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RequestMapping("/set/unit")
public class UnitSettingController {

	@Autowired 
    private IUnitService iUnitService;
	
	@Autowired
	private RedisUtil redisUtil;


    @ApiOperation(value = "单位设置-添加", notes = "单位设置-添加")
    @RequestMapping(value = {"/add"}, method = RequestMethod.POST)
    public ResultObject add(@RequestBody @ApiParam(name = "单位设置", value = "传入json格式", required = true) Unit unit) {
        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            if (unit.getPid() == null) {
                unit.setPid(0);
            }
            if (unit.getName() == null || unit.getName().isEmpty()) {
                resultObject.setMsg("单位名称为空");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else if (iUnitService.unitExist(unit.getPid(), unit.getName(), userInfo.getEnterpriseId())) {
                resultObject.setMsg("单位名称已存在");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else {
            	unit.setUserId(userInfo.getUserId());
            	unit.setEnterpriseId(userInfo.getEnterpriseId());
                if (iUnitService.save(unit)) {
                	resultObject.setData(unit.getUnitId());
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

    @ApiOperation(value = "单位设置-修改", notes = "单位设置-修改")
    @RequestMapping(value = {"/update"}, method = RequestMethod.POST)
    public ResultObject update(@RequestBody @ApiParam(name = "单位设置", value = "传入json格式", required = true) Unit unit) {
        ResultObject resultObject = new ResultObject();
        //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        try {
            if (unit.getPid() == null) {
                unit.setPid(0);
            }
            Unit unitExit = iUnitService.getById(unit.getUnitId());
            if (unit.getName() == null || unit.getName().isEmpty()) {
                resultObject.setMsg("单位名称为空");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else if (!unitExit.getName().equals(unit.getName()) &&
                    iUnitService.unitExist(unit.getPid(), unit.getName(),userInfo.getEnterpriseId())) {
                resultObject.setMsg("单位名称已存在");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else {
            	redisUtil.del("unitName_" + unit.getUnitId());
                if (iUnitService.updateById(unit)) {
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

    @ApiOperation(value = "单位设置-删除", notes = "单位设置-删除")
    @RequestMapping(value = {"/delete"}, method = RequestMethod.GET)
    public ResultObject delete(Integer unitId) {
        ResultObject resultObject = new ResultObject();
        try {
            if (unitId == null || unitId.equals(0)) {
                resultObject.setMsg("参数错误");
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            } else {
            	redisUtil.del("unitName_" + unitId);
                if (iUnitService.removeById(unitId)) {
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
