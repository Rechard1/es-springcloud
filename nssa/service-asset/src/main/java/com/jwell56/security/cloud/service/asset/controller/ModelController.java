package com.jwell56.security.cloud.service.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.entity.Risk;
import com.jwell56.security.cloud.service.asset.entity.CnvdInformation;
import com.jwell56.security.cloud.service.asset.entity.ModelRepository;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.entity.commons.PageParam;
import com.jwell56.security.cloud.service.asset.entity.vo.ModelRepositoryVo;
import com.jwell56.security.cloud.service.asset.service.IModelRepositoryService;
import com.jwell56.security.cloud.service.asset.utils.NssaDateUtils;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/model")
public class ModelController {

    @Autowired
    private IModelRepositoryService iModelRepositoryService;

    @ApiOperation("查询模型知识库")
    @RequestMapping(value = "/getModelPaging", method = RequestMethod.GET)
    public ResultObject getModelPaging(String keyWord,String modelType,String modelName, PageParam pageParam) {
        try {
            QueryWrapper<ModelRepository> modelQueryWrapper = new QueryWrapper<>();
            if(keyWord != null){
                if(NssaDateUtils.parseDate(keyWord) != null){
                    modelQueryWrapper.lambda().eq(ModelRepository::getCreateTime, keyWord);
                }else {
                    modelQueryWrapper.lambda().and(obj -> obj
                            .like(ModelRepository::getModelCode, keyWord).or()
                            .like(ModelRepository::getModelName,keyWord).or()
                            .like(ModelRepository::getRemark, keyWord).or()
                            .like(ModelRepository::getModelType,keyWord));
                }
            }
            if(modelType != null ){
                modelQueryWrapper.lambda().eq(ModelRepository::getModelType, modelType);
            }
            if(modelName != null ){
                modelQueryWrapper.lambda().like(ModelRepository::getModelName, modelName);
            }

            modelQueryWrapper.lambda().orderByDesc(ModelRepository::getCreateTime);
            IPage<ModelRepository> modelPage = iModelRepositoryService.page(pageParam.iPage(), modelQueryWrapper);

            List<ModelRepositoryVo> modelVoList = new ArrayList();
            IPage<ModelRepositoryVo> modelVoPage = new Page<ModelRepositoryVo>();
            BeanUtils.copyProperties(modelPage, modelVoPage);

            for(ModelRepository risk : modelPage.getRecords()) {
                ModelRepositoryVo riskVo = new ModelRepositoryVo();
                BeanUtils.copyProperties(risk, riskVo);
                riskVo.setUserName("系统");
                modelVoList.add(riskVo);
            }
            modelVoPage.setRecords(modelVoList);
            return ResultObject.data(modelVoPage);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("获取模型知识库")
    @GetMapping("getModuleType")
    public ResultObject getModuleType() {
        QueryWrapper<ModelRepository> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("DISTINCT model_type");
        List<ModelRepository> list =  iModelRepositoryService.list(queryWrapper);

        List<String> listResult = list.stream()
                .filter(r-> r != null)
                .filter(r-> r.getModelType() != null)
                .filter(r-> r.getModelType() != "")
                .map(ModelRepository::getModelType)
                .limit(1000)
                .collect(Collectors.toList());

        return ResultObject.data(listResult);
    }

    @ApiOperation("根据模型类型获取模型数据")
    @GetMapping("/model")
    public ResultObject getModuleByModuleType(@RequestParam("modelType") String modelType) {

        QueryWrapper<ModelRepository> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(ModelRepository::getModelType, modelType);
        List<ModelRepository> repositoryList = iModelRepositoryService.list(queryWrapper);

        ResultObject resultObject = new ResultObject();
        resultObject.setCode(200);
        resultObject.setData(repositoryList);
        return resultObject;
    }

}
