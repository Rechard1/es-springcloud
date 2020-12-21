package com.jwell56.security.cloud.service.apt.service.serviceImpl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jwell56.security.cloud.common.entity.Risk;
import com.jwell56.security.cloud.service.apt.entity.Apt;
import com.jwell56.security.cloud.service.apt.entity.ParamPI;
import com.jwell56.security.cloud.service.apt.service.RiskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@Service
public class AptServiceImpl {

    @Autowired
    private ESAptService eSAptService;

    @Autowired
    private RiskService riskService;

    public List<String> selectRiskType(String file) {
        return eSAptService.selectRiskType(file);
    }

    public IPage<Apt> selectApt(ParamPI paramPI) throws IOException, ParseException {
        return eSAptService.searchAptDoc(paramPI);
    }

    public Apt selectAptDetail(String rowkey,String riskType) throws IOException, ParseException {
        String aptlog = eSAptService.selectAptDetailDoc(rowkey);
        Apt apt = new Apt();
        apt.setRawLog(aptlog);
        apt.setRiskType(riskType);
        Risk risk = riskService.getRisk(riskType);
        if(risk != null){
            apt.setRiskDes(risk.getRiskDes());
            apt.setRiskSuggestion(risk.getRiskSuggestion());
        }
        return apt;
    }
}
