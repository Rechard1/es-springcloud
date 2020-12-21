package com.jwell56.security.cloud.service.netstruct.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.entity.Topology;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.entity.vo.TopologyVo;
import com.jwell56.security.cloud.service.netstruct.service.ITopologyService;
import com.jwell56.security.cloud.service.netstruct.utils.RedisUtil;
import com.jwell56.security.cloud.service.netstruct.utils.StatisticsComponent;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/topology/")
public class TopologyController {
	
	@Autowired
	private ITopologyService iTopologyService;
	
    @Autowired
    private StatisticsComponent statisticsComponent;
	
	@Autowired
	private RedisUtil redisUtil;
    
	public static final Integer STATISTICS_VALID_TIME = 86100;//统计缓存5分钟刷新一次

	@GetMapping("getTopologyById")
	public ResultObject getTopologyById(Integer topologyId) {
		return ResultObject.data(iTopologyService.getById(topologyId));
	}
	
    @ApiOperation("拓扑结构")
    @RequestMapping(value = {"/struct"}, method = RequestMethod.GET)
    public ResultObject<Map<String, TopologyVo>> struct(String key) {

    	 //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        String cacheKey = "struct-" + key + "-" + userInfo.getRoleId();
        if (redisUtil.get(cacheKey) == null) {
            statisticsComponent.getStructCacheBase(key, cacheKey, userInfo.getRoleId());
        } else if (redisUtil.getExpire(cacheKey) < STATISTICS_VALID_TIME) {
            statisticsComponent.getStructCacheAsync(key, cacheKey, userInfo.getRoleId());
        }
        ResultObject resultObject = (ResultObject) redisUtil.get(cacheKey);
        return resultObject;
    }
    
    @ApiOperation("拓扑名称")
    @RequestMapping(value = {"/name"}, method = RequestMethod.GET)
    public ResultObject name(String key) {
        try {
            QueryWrapper<Topology> topologyQueryWrapper = new QueryWrapper<>();
            if (key != null && !key.isEmpty()) {
            	topologyQueryWrapper.lambda().eq(Topology::getKeyName, key);
            }
            topologyQueryWrapper.lambda().orderByDesc(Topology::getTopologyId);
            Topology sysTopology = iTopologyService.getOne(topologyQueryWrapper);
            if (sysTopology != null) {
                return ResultObject.value("name", sysTopology.getName());
            } else {
                return ResultObject.badRequest("请求的拓扑节点不存在");
            }
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
}
