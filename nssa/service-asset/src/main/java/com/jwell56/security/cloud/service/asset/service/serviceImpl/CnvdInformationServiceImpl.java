package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.CnvdInformation;
import com.jwell56.security.cloud.service.asset.service.ICnvdInformationService;
import org.springframework.stereotype.Service;

@Service
public class CnvdInformationServiceImpl extends ServiceImpl<BaseMapper<CnvdInformation>, CnvdInformation> implements ICnvdInformationService {

}
