package com.jwell56.security.cloud.service.sso.service.serviceImpl;

import com.jwell56.security.cloud.service.sso.entity.SysUser;
import com.jwell56.security.cloud.service.sso.mapper.SysUserMapper;
import com.jwell56.security.cloud.service.sso.service.ISysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-10-29
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

}
