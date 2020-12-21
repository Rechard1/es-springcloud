package com.jwell56.security.cloud.service.ids.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.ShellUtil;
import com.jwell56.security.cloud.service.ids.entity.SysConfig;
import com.jwell56.security.cloud.service.ids.service.ISysConfigService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 系统配置表 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2020-12-04
 */
@RestController
@RequestMapping("/sys-config")
public class SysConfigController {
    @Autowired
    ISysConfigService iSysConfigService;

    @ApiOperation(value = "配置状态", notes = "type{探针配置,系统版本,许可证书}")
    @RequestMapping(value = {"/info"}, method = RequestMethod.GET)
    @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "配置类型：探针配置,系统版本,许可证书")
    public ResultObject<SysConfig> info(String type) {
        if (type == null || type.isEmpty() || !"探针配置,系统版本,许可证书".contains(type)) {
            return ResultObject.badRequest("请求错误");
        }
        QueryWrapper<SysConfig> sysConfigQueryWrapper = new QueryWrapper<>();
        sysConfigQueryWrapper.lambda().eq(SysConfig::getCfType, type);
        SysConfig sysConfig = iSysConfigService.getOne(sysConfigQueryWrapper);
        sysConfig.setCfFile("");
        sysConfig.setCfShell("");
        return ResultObject.data(sysConfig);
    }

    @ApiOperation(value = "配置文件上传")
    @RequestMapping(value = {"/upload"}, method = RequestMethod.POST)
    @ApiImplicitParam(paramType = "query", dataType = "String", name = "type", value = "配置类型：探针配置,系统版本,许可证书")
    public ResultObject upload(@RequestParam("file") MultipartFile file, String type) {
        String filePath;
        if (type == null || type.isEmpty() || !"探针配置,系统版本,许可证书".contains(type)) {
            return ResultObject.badRequest("请求错误");
        }
        try {
            if (file.isEmpty()) {
                return ResultObject.badRequest("文件为空,请重新选择合法文件");
            } else {
                // 获取文件名
                String fileName = file.getOriginalFilename();
                // 设置文件存储路径
                filePath = (new File("/opt/nssa/config/" + fileName)).getAbsolutePath();
                File dest = new File(filePath);

                // 检测是否存在目录
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();// 新建文件夹
                }

                file.transferTo(dest);// 文件写入
            }
        } catch (IOException e) {
            return ResultObject.badRequest("上传失败");
        }

        QueryWrapper<SysConfig> sysConfigQueryWrapper = new QueryWrapper<>();
        sysConfigQueryWrapper.lambda().eq(SysConfig::getCfType, type);
        SysConfig sysConfig = iSysConfigService.getOne(sysConfigQueryWrapper);
        if (sysConfig == null) {
            return ResultObject.badRequest("请求错误");
        }
        sysConfig.setCfFile(filePath);
        String result = ShellUtil.ExecCommand(sysConfig.getCfShell());
        sysConfig.setCfDes(result.replace("\n", ""));
        iSysConfigService.saveOrUpdate(sysConfig);
        return ResultObject.message("配置成功");
    }
}