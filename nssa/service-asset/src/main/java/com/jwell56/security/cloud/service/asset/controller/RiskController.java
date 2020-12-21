package com.jwell56.security.cloud.service.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.entity.Risk;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.entity.commons.PageParam;
import com.jwell56.security.cloud.service.asset.entity.vo.RiskVo;
import com.jwell56.security.cloud.service.asset.service.RiskService;
import com.jwell56.security.cloud.service.asset.service.feign.IRoleService;
import com.jwell56.security.cloud.service.asset.utils.NssaDateUtils;
import com.jwell56.security.cloud.service.asset.utils.ThreadLocalUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;

    @Autowired
    private IRoleService iRoleService;

    private static String[] parsePatterns = {"yyyy-MM-dd","yyyy年MM月dd日",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy/MM/dd",
            "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyyMMdd"};

    @ApiOperation("查询风险知识库")
    @RequestMapping(value = "/getRiskPaging", method = RequestMethod.GET)
    public ResultObject getRiskPaging(String keyWord, String riskType,String  riskGrade,String riskName,String riskClass,PageParam pageParam) {
        try {
            User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
            QueryWrapper<Risk> riskQueryWrapper = new QueryWrapper<>();
            if(keyWord != null){
                if(NssaDateUtils.parseDate(keyWord) != null){
                    riskQueryWrapper.lambda().eq(Risk::getCreateTime, keyWord);
                }else {
                    if (keyWord.equals("高") || keyWord.equals("中") || keyWord.equals("低")) {
                        riskQueryWrapper.lambda().eq(Risk::getRiskGrade, keyWord);
                    }else{
                        riskQueryWrapper.lambda().and(obj -> obj
                                .like(Risk::getRiskName, keyWord).or()
                                .like(Risk::getRemark, keyWord).or()
                                .like(Risk::getRiskClass, keyWord).or()
                                .like(Risk::getRiskType,keyWord));
                    }
                }
            }
            if(riskType != null ){
                riskQueryWrapper.lambda().eq(Risk::getRiskType, riskType);
            }
            if(riskGrade != null ){
                riskQueryWrapper.lambda().eq(Risk::getRiskGrade, riskGrade);
            }
            if(riskName != null ){
                riskQueryWrapper.lambda().like(Risk::getRiskName, riskName);
            }
            if(riskClass != null ){
                riskQueryWrapper.lambda().like(Risk::getRiskClass, riskClass);
            }
            //可以看系统和同企业下
            List<Integer> listE = new ArrayList<>();
            listE.add(0);
            listE.add(userInfo.getEnterpriseId());
            riskQueryWrapper.lambda().in(Risk::getEnterpriseId,listE);

            riskQueryWrapper.lambda().orderByDesc(Risk::getCreateTime);
            IPage<Risk> riskPage = riskService.page(pageParam.iPage(), riskQueryWrapper);

            List<RiskVo> riskVoList = new ArrayList();
            IPage<RiskVo> riskVoPage = new Page<RiskVo>();
            BeanUtils.copyProperties(riskPage, riskVoPage);

            for(Risk risk : riskPage.getRecords()) {
                RiskVo riskVo = new RiskVo();
                BeanUtils.copyProperties(risk, riskVo);
                if(riskVo.getUserId() == 0){
                    riskVo.setUserName("系统");
                }else{
                    ResultObject res = iRoleService.getUserById(riskVo.getUserId());
                    Map sysUser = (Map) res.getData();
                    if(sysUser != null ) {
                        riskVo.setUserName((String) sysUser.get("username"));
                    }
                }
                riskVoList.add(riskVo);
            }
            riskVoPage.setRecords(riskVoList);
            return ResultObject.data(riskVoPage);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

    @ApiOperation("新增风险知识库")
    @PostMapping("addRisk")
    public ResultObject add(@RequestBody Risk risk) {
        ResultObject res = new ResultObject<>();
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        risk.setUserId(userInfo.getUserId());
        risk.setEnterpriseId(userInfo.getEnterpriseId());
        boolean b = riskService.save(risk);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("新增风险知识库成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增风险知识库失败");
        }
        return res;
    }

    @ApiOperation("删除风险知识库")
    @GetMapping("deleteRisk") //系统的删除不了  直接不显示按钮
    public ResultObject delete(String riskId) {
        ResultObject res = new ResultObject();

        String ids [] = riskId.split(",");
        boolean b =  riskService.removeByIds(Arrays.asList(ids));
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("删除风险知识库成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除风险知识库失败");
        }

        return res;
    }

    @ApiOperation("修改风险知识库") //前端页面只能看到系统和本企业的 所以不用去关心那个企业
    @RequestMapping(value = "/updateRisk", method = RequestMethod.POST)
    public ResultObject updateRisk(@RequestBody Risk risk) {
        ResultObject res = new ResultObject();

        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        risk.setUserId(userInfo.getUserId());
        risk.setEnterpriseId(userInfo.getEnterpriseId());
        boolean b = riskService.saveOrUpdate(risk);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("修改成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改失败");
        }
        return res;
    }

    @ApiOperation("获取知识库")
    @GetMapping("getRiskType")
    public ResultObject getRiskType(String file) {
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<Risk> queryWrapper = new QueryWrapper<>();
        List<Integer> listE = new ArrayList<>();
        listE.add(0);
        listE.add(userInfo.getEnterpriseId());
        queryWrapper.lambda().in(Risk::getEnterpriseId,listE);
        queryWrapper.select("DISTINCT "+file);
        List<Risk> list =  riskService.list(queryWrapper);

        List<String> listResult = new ArrayList<>();
        if(file.equals("risk_type")){
            listResult = list.stream()
                    .filter(r-> r != null)
                    .filter(r-> r.getRiskType() != null)
                    .filter(r-> r.getRiskType() != "")
                    .map(Risk::getRiskType)
                    .limit(1000)
                    .collect(Collectors.toList());
        }else if(file.equals("risk_class")){
            listResult = list.stream()
                    .filter(r-> r != null)
                    .filter(r-> r.getRiskClass() != null)
                    .filter(r-> r.getRiskClass() != "")
                    .map(Risk::getRiskClass)
                    .limit(1000)
                    .collect(Collectors.toList());
        }

//        for(Risk risk : list){
//            if(risk ！=null && risk.getRiskType() != null ){
//                listResult.add(risk.getRiskType());
//            }
//        }
//        listResult.remove("");
        return ResultObject.data(listResult);
    }
}
