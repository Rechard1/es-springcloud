package com.jwell56.security.cloud.service.netstruct.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.entity.Topology;
import com.jwell56.security.cloud.service.netstruct.entity.vo.TopologyVo;
import com.jwell56.security.cloud.service.netstruct.service.ITopologyService;

/**
 * @author wsg
 * @since 2020/1/2
 */
@Component
@EnableAsync
public class StatisticsComponent {

    @Autowired
    private RedisUtil redisUtil;
    
	@Autowired
	private ITopologyService iTopologyService;
	
    public static final Integer STATISTICS_CACHE_TIME = 86400;//统计缓存保留1天

    @Async
    public ResultObject<Map<String, TopologyVo>> getStructCacheAsync(String key, String cacheKey, Integer roleId) {
        return getStructCacheBase(key, cacheKey, roleId);
    }

    public ResultObject<Map<String, TopologyVo>> getStructCacheBase(String key, String cacheKey, Integer roleId) {
        try {
            Map<String, TopologyVo> struct = new HashMap<>();
            int pid = 0;
            if (key != null && !key.isEmpty()) {
                QueryWrapper<Topology> topologyQueryWrapper = new QueryWrapper<>();
                topologyQueryWrapper.lambda().eq(Topology::getKeyName, key);
                Topology topology = iTopologyService.getOne(topologyQueryWrapper);
                if (topology != null) {
                    pid = topology.getTopologyId();
                } else {
                    return ResultObject.badRequest("请求的拓扑节点不存在");
                }
            }

            //获取拓扑结构
            QueryWrapper<Topology> topologyQueryWrapper = new QueryWrapper<>();
            topologyQueryWrapper.lambda().eq(Topology::getPid, pid);
            List<Topology> topologyList = iTopologyService.list(topologyQueryWrapper);
            if (topologyList == null || topologyList.isEmpty()) {
                return ResultObject.badRequest("无子节点数据");
            }

            for (Topology topology : topologyList) {
                TopologyVo topologyVo = new TopologyVo();
                struct.put(topology.getKeyName(), topologyVo);
                topologyVo.setAreaIdList(topology.getAreaId().toString());
                topologyVo.setUnitIdList(topology.getUnitId().toString());
                topologyVo.setKeyName(topology.getKeyName());
                topologyVo.setName(topology.getName());

//                //报警消息
//                List<AlertVo> alertVoList = new ArrayList<>();
//                topologyVo.setAlert(alertVoList);
//
//                if (Topology.getAccess() == 1) {
//
//                    AUParam auParam = new AUParam();
//                    auParam.setRoleId(roleId);
//
//                    //报警范围
//                    LocalDateTime startTime = iBigSetService.getStart("安全事件");
//                    LocalDateTime endTime = LocalDateTime.now();
//                    auParam.setAreaIdList(topologyVo.getAreaIdList());
//                    auParam.setUnitIdList(topologyVo.getUnitIdList());
//                    //如果是具体的设备，根据IP搜索
//                    boolean isDevice = sysTopology.getStartIp() != null && !sysTopology.getStartIp().isEmpty() &&
//                            sysTopology.getEndIp() != null && !sysTopology.getEndIp().isEmpty();
//
//                    //威胁消息
//                    QueryWrapper<Apt> aptQueryWrapper = new QueryWrapper<>();
//                    aptQueryWrapperComponent.queryWrapperForAreaUnit(
//                            auParam.areaIdList(), auParam.unitIdList(), false, false, aptQueryWrapper);
//                    if (isDevice) {
//                        aptQueryWrapper.lambda().and(obj -> obj
//                                .between(Apt::getSIpNum, IPUtils.ipToLong(sysTopology.getStartIp()), IPUtils.ipToLong(sysTopology.getEndIp())).or()
//                                .between(Apt::getDIpNum, IPUtils.ipToLong(sysTopology.getStartIp()), IPUtils.ipToLong(sysTopology.getEndIp())));
//                    }
////                    aptQueryWrapper.lambda().groupBy(Apt::getSIp, Apt::getDIp, Apt::getAttackType);
//                    aptQueryWrapper.last("limit 0,5");
//                    aptQueryWrapper.lambda().between(Apt::getHappenTime, startTime, endTime);
//                    aptQueryWrapper.lambda().orderByDesc(Apt::getHappenTime);
//                    List<Apt> aptList = iAptService.list(aptQueryWrapper);
//                    for (Apt apt : aptList) {
//                        AlertVo alertVo = new AlertVo();
//                        alertVo.setId(apt.getId());
//                        alertVo.setType("apt");
//                        switch (apt.getAttackGrade()) {
//                            case Apt.ATTACK_STAGE_HIGH:
//                                alertVo.setGrade(3);
//                                break;
//                            case Apt.ATTACK_STAGE_MID:
//                                alertVo.setGrade(2);
//                                break;
//                            case Apt.ATTACK_STAGE_LOW:
//                                alertVo.setGrade(1);
//                                break;
//                            default:
//                                alertVo.setGrade(0);
//                        }
//                        alertVo.setDes(apt.getAttackType());
//                        Asset asset = iAssetService.getAsset(apt.getSIp(), auParam.areaIdList(), auParam.unitIdList());
//                        alertVo.setAsset(asset == null || asset.getName() == null || asset.getName().isEmpty() ? apt.getSIp() : asset.getName());
//                        alertVoList.add(alertVo);
//                    }
//
//                    //异常消息
//                    QueryWrapper<AnomalyDevice> anomalyDeviceQueryWrapper = new QueryWrapper<>();
//                    anomalyDeviceQueryWrapper.lambda().between(AnomalyDevice::getHappenTime, startTime, endTime);
//                    anomalyDeviceQueryWrapper = iAnomalyDeviceService.queryWrapperForAreaUnit(
//                            auParam.areaIdList(), auParam.unitIdList(), anomalyDeviceQueryWrapper);
//                    anomalyDeviceQueryWrapper.last("limit 0,5");
//                    anomalyDeviceQueryWrapper.lambda().orderByDesc(AnomalyDevice::getHappenTime);
//                    if (isDevice) {
//                        QueryWrapper<Material> materialQueryWrapper = new QueryWrapper<>();
//                        String sql = "INET_ATON(ip) between " + IPUtils.ipToLong(sysTopology.getStartIp()) + " and " +
//                                IPUtils.ipToLong(sysTopology.getEndIp());
//                        materialQueryWrapper.apply(sql);
//                        List<Material> materialList = iMaterialService.list(materialQueryWrapper);
//                        int assetId = 0;
//                        for (Material material : materialList) {
//                            if (auParam.areaIdList().contains(material.getAreaId()) &&
//                                    auParam.unitIdList().contains(material.getUnitId())) {
//                                assetId = material.getId();
//                            }
//                        }
//                        anomalyDeviceQueryWrapper.lambda().eq(AnomalyDevice::getAssetId, assetId);
//                    }
//                    List<AnomalyDevice> deviceList = iAnomalyDeviceService.list(anomalyDeviceQueryWrapper);
//                    for (AnomalyDevice anomalyDevice : deviceList) {
//                        AlertVo alertVo = new AlertVo();
//                        alertVo.setId(anomalyDevice.getAssetId());
//                        alertVo.setType("anomaly");
//                        String aKey = "";
//                        Integer health = 0;
//                        Integer value = 0;
//                        double flow = 0;
//                        switch (anomalyDevice.getMarkedType()) {
//                            case AnomalyDevice.MARKED_TYPE_CPU:
//                                health = anomalyDevice.getAssetCpu();
//                                value = anomalyDevice.getAssetCpu();
//                                aKey = "cpu";
//                                break;
//                            case AnomalyDevice.MARKED_TYPE_MEMORY:
//                                health = anomalyDevice.getAssetMemory();
//                                value = anomalyDevice.getAssetMemory();
//                                aKey = "内存";
//                                break;
//                            case AnomalyDevice.MARKED_TYPE_DISK:
//                                health = anomalyDevice.getAssetDisk();
//                                value = anomalyDevice.getAssetDisk();
//                                aKey = "磁盘";
//                                break;
//                            case AnomalyDevice.MARKED_TYPE_FLOW_DOWN:
//                                flow = anomalyDevice.getAssetFlowDown();
//                                aKey = "流量";
//                                break;
//                            case AnomalyDevice.MARKED_TYPE_FLOW_UP:
//                                flow = anomalyDevice.getAssetFlowUp();
//                                aKey = "流量";
//                                break;
//                        }
//                        health = health == 0 ? 50 : (100 - health * health * health / 10000);
//                        int level = health < 25 ? 4 : health < 50 ? 3 : health < 75 ? 2 : 1;
//                        DecimalFormat df = new DecimalFormat("#.00");
//                        String flowStr = flow > (1024 * 1024) ? df.format(flow / 1024 / 1024) + "PB" :
//                                flow > 1024 ? df.format(flow / 1024) + "GB" : df.format(flow) + "MB";
//                        alertVo.setGrade(level - 1);
//                        alertVo.setDes(aKey + (value == 0 ? "" : value + "%") + (flow == 0 ? "" : flowStr));
//                        Material material = iMaterialService.getById(alertVo.getId());
//                        alertVo.setAsset(material == null ? "" : material.getName());
//                        alertVoList.add(alertVo);
//                    }
//
//                    //基于威胁数据，定节点的安全等级
//                    int high = iAptService.getGradeCount(1, auParam.areaIdList(), auParam.unitIdList(),
//                            startTime, endTime, Apt.ATTACK_STAGE_HIGH, null);
//
//                    int mid = iAptService.getGradeCount(1, auParam.areaIdList(), auParam.unitIdList(),
//                            startTime, endTime, Apt.ATTACK_STAGE_MID, null);
//
//                    int low = iAptService.getGradeCount(1, auParam.areaIdList(), auParam.unitIdList(),
//                            startTime, endTime, Apt.ATTACK_STAGE_LOW, null);
//
//                    int level;
//                    if (high > 100) {
//                        level = 4;
//                    } else if (high > 0) {
//                        level = 3;
//                    } else if (mid > 0) {
//                        level = 2;
//                    } else if (low > 0) {
//                        level = 1;
//                    } else {
//                        level = 0;
//                    }
//                    topologyVo.setLevel(level);
//                } else {
//                    topologyVo.setLevel(0);
//                }
//                QueryWrapper<SysTopology> sysTopologyChildQueryWrapper = new QueryWrapper<>();
//                sysTopologyChildQueryWrapper.lambda().eq(SysTopology::getPid, sysTopology.getId());
//                topologyVo.setChild(!iSysTopologyService.list(sysTopologyChildQueryWrapper).isEmpty());
//
//                topologyVo.setAccess(sysTopology.getAccess() == 1);
//
//            }
//
            redisUtil.set(cacheKey, ResultObject.data(struct), STATISTICS_CACHE_TIME);
            }
            return ResultObject.data(struct);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
}
