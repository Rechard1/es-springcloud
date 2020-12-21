package com.jwell56.security.cloud.service.role.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.role.entity.Module;
import com.jwell56.security.cloud.service.role.entity.TreeNode;

/**
 * <p>
 * 功能模块表 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-16
 */
public interface IModuleService extends IService<Module> {

	List<TreeNode> treeList();
}
