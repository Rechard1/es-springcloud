package com.jwell56.security.cloud.service.role.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.role.entity.Module;
import com.jwell56.security.cloud.service.role.entity.TreeNode;
import com.jwell56.security.cloud.service.role.mapper.ModuleMapper;
import com.jwell56.security.cloud.service.role.service.IModuleService;
import com.jwell56.security.cloud.service.role.utils.TreeNodeUtils;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModuleMapper moduleMapper;

    @Override
    public List<TreeNode> treeList() {

    	QueryWrapper<Module> wrapper = new QueryWrapper<Module>();
		wrapper.lambda().ne(Module :: getModuleName, "企业管理").ne(Module :: getPId, 28);
        List<Module> moduleList = this.list(wrapper);
        List<TreeNode> treeNodeList = new LinkedList<>();
        for (Module module : moduleList) {
        	if(module.getPId() == 0) {
        		TreeNode treeNode = new TreeNode(module.getModuleId(),
        				module.getPId(),
        				module.getModuleName(),
        				module.getBasePath(),module.getNav());
        		treeNodeList.add(treeNode);
        	}else {
        		TreeNode treeNode = new TreeNode(module.getModuleId(),
        				module.getPId(),
        				module.getModuleName(),
        				module.getBasePath());
        		treeNodeList.add(treeNode);
        	}
        }

        //重新组织成树状
        List<TreeNode> nodeList = TreeNodeUtils.getTreeList(treeNodeList, 0);

        return nodeList;
    }
}
