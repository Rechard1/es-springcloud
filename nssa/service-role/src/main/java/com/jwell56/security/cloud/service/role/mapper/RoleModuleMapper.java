package com.jwell56.security.cloud.service.role.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwell56.security.cloud.service.role.entity.Module;
import com.jwell56.security.cloud.service.role.entity.RoleModule;
import com.jwell56.security.cloud.service.role.entity.RoleModuleVo;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author RonnieXu
 * @since 2019-04-15
 */

@Repository
public interface RoleModuleMapper extends BaseMapper<RoleModule> {

    List<Module> getRoleModuleVoList(@Param("roleId") Integer roleId);
}
