package com.jwell56.security.cloud.service.netstruct.service.serviceImpl;

import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.netstruct.common.IPUtil;
import com.jwell56.security.cloud.service.netstruct.entity.NetStruct;
import com.jwell56.security.cloud.service.netstruct.entity.NetStructEx;
import com.jwell56.security.cloud.service.netstruct.entity.dto.NetStructDto;
import com.jwell56.security.cloud.service.netstruct.mapper.NetStructMapper;
import com.jwell56.security.cloud.service.netstruct.service.INetStructService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
@Service
public class NetStructServiceImpl extends ServiceImpl<NetStructMapper, NetStruct> implements INetStructService {
    @Resource
    NetStructMapper netStructMapper;


    @Override
    public NetStructDto getNetStructDto(String ip, Integer areaId, Integer unitId) {
        Map<String, Object> map = new HashMap<>();
        map.put("ip", ip);
        map.put("areaId", areaId);
        map.put("unitId", unitId);
        List<NetStructDto> netStructDtoList = netStructMapper.selectNetStructDtoList(map);

        Map<Long, NetStructDto> resultMap = new HashMap<>();
        netStructDtoList.forEach(netStructDto ->
                resultMap.put(IPUtil.ipToLong(netStructDto.getEndIp()) - IPUtil.ipToLong(netStructDto.getStartIp()), netStructDto));

        return resultMap.isEmpty() ? null : resultMap.get(Collections.min(resultMap.keySet()));
    }

    @Override
    public List<NetStruct> listCache() {
        List<NetStruct> netStructList = (List<NetStruct>) CommonCachePool.getData("netStructCache");
        if (netStructList == null) {
            netStructList = this.list(null);
            CommonCachePool.setData("netStructCache", netStructList);
        }
        return netStructList;
    }

    @Override
    public List<NetStructEx> listExCache(Integer enterpriseId) {
        List<NetStructEx> netStructExList = (List<NetStructEx>) CommonCachePool.getData("netStructExCache");
        if (netStructExList == null) {
        	QueryWrapper<NetStruct> wrapper = new QueryWrapper<NetStruct>();
        	wrapper.lambda().eq(NetStruct :: getEnterpriseId, enterpriseId);
            List<NetStruct> netStructList = this.list(wrapper);
            netStructExList = new ArrayList<>();
            for (NetStruct netStruct : netStructList) {
                NetStructEx netStructEx = new NetStructEx();
                BeanUtils.copyProperties(netStruct, netStructEx);
                netStructEx.setSIpNum(IPUtil.ipToLong(netStructEx.getStartIp()));
                netStructEx.setDIpNum(IPUtil.ipToLong(netStructEx.getEndIp()));
                netStructExList.add(netStructEx);
            }
            CommonCachePool.setData("netStructExCache", netStructExList);
        }
        return netStructExList;
    }
}
