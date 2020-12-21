package com.jwell56.security.cloud.service.role.controller;


import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.role.entity.TreeNode;
import com.jwell56.security.cloud.service.role.service.IModuleService;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 功能模块表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-11-16
 */
@RestController
@RequestMapping("/module")
public class ModuleController {

	@Autowired
	private IModuleService iModuleService;
	
	 /**
     * 功能模块树状列表
     * @return
     */
    @ApiOperation(value = "功能模块树状列表", notes = "功能模块树状列表")
    @RequestMapping(value = {"/treelist"}, method = RequestMethod.GET)
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
            }
    )
    public ResultObject treelist(){

        ResultObject resultObject = new ResultObject();

        try {
            List<TreeNode> moduleList = iModuleService.treeList();

            resultObject.setData(moduleList);
            resultObject.setCode(HttpServletResponse.SC_OK);

        }catch (Exception e){
            resultObject.setMsg(e.getMessage());
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return resultObject;
    }

}
