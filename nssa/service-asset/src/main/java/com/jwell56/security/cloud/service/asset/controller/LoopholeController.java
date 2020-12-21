package com.jwell56.security.cloud.service.asset.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.entity.Risk;
import com.jwell56.security.cloud.service.asset.entity.CnvdInformation;
import com.jwell56.security.cloud.service.asset.entity.ModelRepository;
import com.jwell56.security.cloud.service.asset.entity.User;
import com.jwell56.security.cloud.service.asset.entity.commons.PageParam;
import com.jwell56.security.cloud.service.asset.entity.vo.CnvdInformationVo;
import com.jwell56.security.cloud.service.asset.service.ICnvdInformationService;
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
@RequestMapping("/loophole")
public class LoopholeController {

    @Autowired
    private ICnvdInformationService iCnvdInformationService;

    @Autowired
    private IRoleService iRoleService;

    @ApiOperation("查询漏洞知识库")
    @RequestMapping(value = "/getLoopholePaging", method = RequestMethod.GET)
    public ResultObject getRiskPaging(String keyWord,String loopholeType, String  loopholeGrade, String loopholeName, PageParam pageParam) {
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<CnvdInformation> modelQueryWrapper = new QueryWrapper<>();
        if(keyWord != null){
            if(NssaDateUtils.parseDate(keyWord) != null){
                modelQueryWrapper.lambda().eq(CnvdInformation::getCreateTime, keyWord);
            }else {
                if (keyWord.equals("高") || keyWord.equals("中") || keyWord.equals("低")) {
                    modelQueryWrapper.lambda().eq(CnvdInformation::getGrade, keyWord);
                }else{
                    modelQueryWrapper.lambda().and(obj -> obj
                            .like(CnvdInformation::getName, keyWord).or()
                            .like(CnvdInformation::getRemark, keyWord).or()
                            .like(CnvdInformation::getType,keyWord));
                }
            }
        }
        if(loopholeType != null ){
            modelQueryWrapper.lambda().eq(CnvdInformation::getType, loopholeType);
        }
        if(loopholeGrade != null ){
            modelQueryWrapper.lambda().like(CnvdInformation::getGrade, loopholeGrade);
        }
        if(loopholeName != null ){
            modelQueryWrapper.lambda().like(CnvdInformation::getName, loopholeName);
        }
        //可以看系统和同企业下
        List<Integer> listE = new ArrayList<>();
        listE.add(0);
        listE.add(userInfo.getEnterpriseId());
        modelQueryWrapper.lambda().in(CnvdInformation::getEnterpriseId,listE);

        modelQueryWrapper.lambda().orderByDesc(CnvdInformation::getCreateTime);
        IPage<CnvdInformation> riskPage = iCnvdInformationService.page(pageParam.iPage(), modelQueryWrapper);

        List<CnvdInformationVo> riskVoList = new ArrayList();
        IPage<CnvdInformationVo> riskVoPage = new Page<>();
        BeanUtils.copyProperties(riskPage, riskVoPage);

        for(CnvdInformation cnvdInformation : riskPage.getRecords()) {
            CnvdInformationVo cnvdInformationVo = new CnvdInformationVo();
            BeanUtils.copyProperties(cnvdInformation, cnvdInformationVo);
            if(cnvdInformationVo.getUserId() == 0){
                cnvdInformationVo.setUserName("系统");
            }else{
                ResultObject res = iRoleService.getUserById(cnvdInformationVo.getUserId());
                Map sysUser = (Map) res.getData();
                if(sysUser != null ) {
                    cnvdInformationVo.setUserName((String) sysUser.get("username"));
                }
            }
            riskVoList.add(cnvdInformationVo);
        }
        riskVoPage.setRecords(riskVoList);
        return ResultObject.data(riskVoPage);
    }

    @ApiOperation("新增漏洞知识库")
    @PostMapping("addLoophole")
    public ResultObject add(@RequestBody CnvdInformation cnvdInformation) {
        ResultObject res = new ResultObject<>();
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        cnvdInformation.setUserId(userInfo.getUserId());
        cnvdInformation.setEnterpriseId(userInfo.getEnterpriseId());
        boolean b = iCnvdInformationService.save(cnvdInformation);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("新增漏洞知识库成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增漏洞知识库失败");
        }
        return res;
    }

    @ApiOperation("删除漏洞知识库")
    @GetMapping("deleteLoophole") //系统的删除不了  直接不显示按钮
    public ResultObject delete(String loopholeId) {
        ResultObject res = new ResultObject();

        String ids [] = loopholeId.split(",");
        boolean b =  iCnvdInformationService.removeByIds(Arrays.asList(ids));
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setMsg("删除漏洞知识库成功");

        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除漏洞知识库失败");
        }

        return res;
    }

    @ApiOperation("修改漏洞知识库") //前端页面只能看到系统和本企业的 所以不用去关心那个企业
    @RequestMapping(value = "/updateLoophole", method = RequestMethod.POST)
    public ResultObject updateLoophole(@RequestBody CnvdInformation cnvdInformation) {
        ResultObject res = new ResultObject();

        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        cnvdInformation.setUserId(userInfo.getUserId());
        cnvdInformation.setEnterpriseId(userInfo.getEnterpriseId());
        boolean b = iCnvdInformationService.saveOrUpdate(cnvdInformation);
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
    @GetMapping("getLoopholeType")
    public ResultObject getLoopholeType() {
        User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        QueryWrapper<CnvdInformation> queryWrapper = new QueryWrapper<>();
        List<Integer> listE = new ArrayList<>();
        listE.add(0);
        listE.add(userInfo.getEnterpriseId());
        queryWrapper.lambda().in(CnvdInformation::getEnterpriseId,listE);
        queryWrapper.select("DISTINCT type");
        List<CnvdInformation> list =  iCnvdInformationService.list(queryWrapper);

        List<String> listResult = list.stream()
                .filter(r-> r != null)
                .filter(r-> r.getType() != null)
                .filter(r-> r.getType() != "")
                .map(CnvdInformation::getType)
                .limit(1000)
                .collect(Collectors.toList());

        return ResultObject.data(listResult);
    }
}
