package com.jwell56.security.cloud.service.apt.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.common.entity.Risk;

import java.util.List;

public interface RiskService extends IService<Risk> {
    public Risk getRisk(String riskType);

    List<Risk> getRisks(String riskNamaes);
}
