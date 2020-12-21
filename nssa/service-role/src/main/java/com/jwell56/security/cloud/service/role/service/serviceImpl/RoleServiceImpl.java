package com.jwell56.security.cloud.service.role.service.serviceImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.role.entity.Module;
import com.jwell56.security.cloud.service.role.entity.Role;
import com.jwell56.security.cloud.service.role.entity.RoleArea;
import com.jwell56.security.cloud.service.role.entity.RoleModule;
import com.jwell56.security.cloud.service.role.entity.RoleUnit;
import com.jwell56.security.cloud.service.role.mapper.ModuleMapper;
import com.jwell56.security.cloud.service.role.mapper.RoleAreaMapper;
import com.jwell56.security.cloud.service.role.mapper.RoleModuleMapper;
import com.jwell56.security.cloud.service.role.mapper.RoleUnitMapper;
import com.jwell56.security.cloud.service.role.service.IRoleAreaService;
import com.jwell56.security.cloud.service.role.service.IRoleModuleService;
import com.jwell56.security.cloud.service.role.service.IRoleService;
import com.jwell56.security.cloud.service.role.service.IRoleUnitService;
import com.jwell56.security.cloud.service.role.utils.StringIdsUtil;

@Service
public class RoleServiceImpl extends ServiceImpl<BaseMapper<Role>, Role> implements IRoleService{

    @Autowired
    private ModuleMapper moduleMapper;

    @Autowired
    private RoleModuleMapper roleModuleMapper;

    @Autowired
    private RoleAreaMapper roleAreaMapper;

    @Autowired
    private RoleUnitMapper roleUnitMapper;
	
	@Autowired
    private IRoleAreaService iRoleAreaService;

    @Autowired
    private IRoleUnitService iRoleUnitService;
    
    @Autowired
    private IRoleModuleService iRoleModuleService;
    
	@Override
	public void saveRole(String unitList, String areaList, String moduleList, Integer roleId) {
		List<RoleArea> roleAreaList = new ArrayList<RoleArea>();
		List<RoleUnit> roleUnitList = new ArrayList<RoleUnit>();
		List<RoleModule> roleModuleList = new ArrayList<RoleModule>();
		for(Integer id : StringIdsUtil.listIds(unitList)) {
			RoleUnit roleUnit = new RoleUnit();
			roleUnit.setRoleId(roleId);
			roleUnit.setUnitId(id);
			roleUnitList.add(roleUnit);
		}
		for(Integer id : StringIdsUtil.listIds(areaList)) {
			RoleArea roleArea = new RoleArea();
			roleArea.setRoleId(roleId);
			roleArea.setAreaId(id);
			roleAreaList.add(roleArea);
		}
		for(Integer id : StringIdsUtil.listIds(moduleList)) {
			RoleModule roleModule = new RoleModule();
			roleModule.setRoleId(roleId);
			roleModule.setModuleId(id);
			roleModuleList.add(roleModule);
		}
		if(!roleAreaList.isEmpty()) {
			iRoleAreaService.saveBatch(roleAreaList);
		}
		if(!roleUnitList.isEmpty()) {
			iRoleUnitService.saveBatch(roleUnitList);
		}
		if(!roleModuleList.isEmpty()) {
			iRoleModuleService.saveBatch(roleModuleList);
		}
	}
	
    @Override
    public Map<String, Object> detail(Integer roleId) {

        Map<String, Object> dataMap = new HashMap<>();

        //module模块
        QueryWrapper<RoleModule> roleModuleQueryWrapper = new QueryWrapper<>();

        roleModuleQueryWrapper.lambda().eq(RoleModule::getRoleId, roleId);
        List<RoleModule> roleModuleList = roleModuleMapper.selectList(roleModuleQueryWrapper);

//        List<Integer> moduleIdList = roleModuleList.stream().map(RoleModule::getModuleId)
//                .filter(x -> x !=null )
//                .collect(Collectors.toList());
        List<Integer> moduleIdList = new ArrayList<>();
        for (RoleModule roleModule : roleModuleList) {
            Module module = moduleMapper.selectById(roleModule.getModuleId());
            if (module.getPId() == 0) {
                continue;
            }
            moduleIdList.add(roleModule.getModuleId());
        }
        dataMap.put("module", moduleIdList);

        //area模块
        QueryWrapper<RoleArea> roleAreaQueryWrapper = new QueryWrapper<>();
        roleAreaQueryWrapper.lambda().eq(RoleArea::getRoleId, roleId);
        List<RoleArea> roleAreaList = roleAreaMapper.selectList(roleAreaQueryWrapper);
        List<Integer> areaIdList = roleAreaList.stream().map(RoleArea::getAreaId).filter(x -> x != null)
                .collect(Collectors.toList());
      //有权限的子节点去掉父节点，以便配合界面显示
//        List<Integer> areaPidList = new ArrayList<>();
//        for (RoleArea roleArea : roleAreaList) {
//            AreaNav areaNav = iAreaNavService.getById(roleArea.getAreaId());
//            if (areaNav != null) {
//                areaPidList.add(areaNav.getPid());
//            }
//        }
//        areaIdList.removeAll(areaPidList);
        dataMap.put("area", areaIdList);

        //unit模块
        QueryWrapper<RoleUnit> roleUnitQueryWrapper = new QueryWrapper<>();
        roleUnitQueryWrapper.lambda().eq(RoleUnit::getRoleId, roleId);
        List<RoleUnit> roleUnitList = roleUnitMapper.selectList(roleUnitQueryWrapper);
        List<Integer> unitIdList = roleUnitList.stream().map(RoleUnit::getUnitId).filter(x -> x != null)
                .collect(Collectors.toList());
        //有权限的子节点去掉父节点，以便配合界面显示
//        List<Integer> unitPidList = new ArrayList<>();
//        for (RoleUnit roleUnit : roleUnitList) {
//            UnitNav unitNav = iUnitNavService.getById(roleUnit.getUnitId());
//            if (unitNav != null) {
//                unitPidList.add(unitNav.getPid());
//            }
//        }
//        unitIdList.removeAll(unitPidList);
        dataMap.put("unit", unitIdList);

        return dataMap;
    }
}
