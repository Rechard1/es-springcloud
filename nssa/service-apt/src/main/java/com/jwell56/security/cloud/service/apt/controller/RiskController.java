package com.jwell56.security.cloud.service.apt.controller;

import com.alibaba.fastjson.JSONArray;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.apt.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private RiskService riskService;


    @GetMapping("/risks/names")
    public ResultObject getRisks(
            @RequestParam("riskNames") String riskNames
    ) {
        ResultObject resultObject = new ResultObject();
        resultObject.setData(JSONArray.toJSONString(riskService.getRisks(riskNames)));
        return resultObject;
    }

}
