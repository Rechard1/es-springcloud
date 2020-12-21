package com.jwell56.security.cloud.service.ids.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.ids.annotation.FieldSettingProperty;
import com.jwell56.security.cloud.service.ids.common.ReflectUtil;
import com.jwell56.security.cloud.service.ids.entity.*;
import com.jwell56.security.cloud.service.ids.entity.vo.FiledVo;
import com.jwell56.security.cloud.service.ids.service.IFiledSettingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2020-12-10
 */
@Api(tags = "安全检测->字段配置")
@RestController
@RequestMapping("/filedSetting")
public class FiledSettingController {

    @Autowired
    private IFiledSettingService iFiledSettingService;

    @ApiOperation("查询字段设置")
    @GetMapping("getFiledSetting")
    public ResultObject getFiledSetting(FiledSearch filedSearch) {
        QueryWrapper<FiledSetting> filedSettingQueryWrapper = new QueryWrapper<>();
        filedSettingQueryWrapper.lambda().eq(FiledSetting::getUserId, filedSearch.getUserId());
        filedSettingQueryWrapper.lambda().eq(FiledSetting::getType, filedSearch.getType());
        FiledSetting filedSetting = iFiledSettingService.getOne(filedSettingQueryWrapper);
        if (filedSetting == null) {
            FiledSetting filedSettingDefault = new FiledSetting();
            filedSettingDefault.setUserId(filedSearch.getUserId());
            filedSettingDefault.setSelfAdaption(0);
            filedSettingDefault.setPage(10);
            filedSettingDefault.setType(filedSearch.getType());
            filedSettingDefault.setFiled(JSONObject.toJSONString(defaultField(filedSearch.getType())));
            boolean def = iFiledSettingService.save(filedSettingDefault);
            return def ? ResultObject.data(filedSettingDefault) : ResultObject.badRequest("请求异常");
        } else {
            return ResultObject.data(filedSetting);
        }
    }

    @ApiOperation("全文检索查询默认字段设置")
    @GetMapping("getAllFiledSettingDefalut")
    public ResultObject getAllFiledSettingDefault(FiledSearch filedSearch) {
        return ResultObject.data(JSONObject.toJSONString(defaultField(filedSearch.getType())));
    }

    private Map<String, Map<String, FiledVo>> defaultField(String type) {
        Map<String, Map<String, FiledVo>> map = new LinkedHashMap<>();

        try {
            //参数为 ids{实体类名}
            Class<?> clazz = Class.forName("com.jwell56.security.cloud.service.ids.entity." + type.substring(3));
            if (clazz == null) return map;
            for (Field field : ReflectUtil.getFieldsWithSupper(clazz)) {
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                FieldSettingProperty fieldSettingProperty = field.getAnnotation(FieldSettingProperty.class);
                if (apiModelProperty != null && fieldSettingProperty != null && !"".equals(fieldSettingProperty.group())) {
                    String key = fieldSettingProperty.group();
                    map.computeIfAbsent(key, k -> new LinkedHashMap<>());
                    map.get(key).put(field.getName(), new FiledVo(apiModelProperty.value(), fieldSettingProperty.isDefault()));
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return map;
    }

    @ApiOperation("更新字段设置")
    @PostMapping("updateFiledSetting")
    public ResultObject updateFieldSetting(@RequestBody FiledSetting filedSetting) {
        filedSetting.setUserId(null);
        return ResultObject.message("更新字段设置", iFiledSettingService.updateById(filedSetting));
    }
}
