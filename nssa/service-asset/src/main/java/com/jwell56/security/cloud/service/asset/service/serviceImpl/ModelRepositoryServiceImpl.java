package com.jwell56.security.cloud.service.asset.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.asset.entity.ModelRepository;
import com.jwell56.security.cloud.service.asset.mapper.ModelRepositoryMapper;
import com.jwell56.security.cloud.service.asset.service.IModelRepositoryService;
import org.springframework.stereotype.Service;

@Service
public class ModelRepositoryServiceImpl  extends ServiceImpl<ModelRepositoryMapper, ModelRepository> implements IModelRepositoryService {
}
