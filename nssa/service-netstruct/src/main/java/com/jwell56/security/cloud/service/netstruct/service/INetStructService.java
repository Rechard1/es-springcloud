package com.jwell56.security.cloud.service.netstruct.service;

import com.jwell56.security.cloud.service.netstruct.entity.NetStruct;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jwell56.security.cloud.service.netstruct.entity.NetStructEx;
import com.jwell56.security.cloud.service.netstruct.entity.dto.NetStructDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
public interface INetStructService extends IService<NetStruct> {
    NetStructDto getNetStructDto(String ip, Integer areaId, Integer unitId);

    List<NetStruct> listCache();

    List<NetStructEx> listExCache(Integer enterpriseId);
}
