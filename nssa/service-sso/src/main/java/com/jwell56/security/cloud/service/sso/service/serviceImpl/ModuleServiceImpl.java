package com.jwell56.security.cloud.service.sso.service.serviceImpl;

import com.jwell56.security.cloud.service.sso.entity.Module;
import com.jwell56.security.cloud.service.sso.mapper.ModuleMapper;
import com.jwell56.security.cloud.service.sso.service.IModuleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 功能模块表 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-16
 */
@Service
public class ModuleServiceImpl extends ServiceImpl<ModuleMapper, Module> implements IModuleService {

}
