package com.jwell56.security.cloud.service.netstruct.mapper;

import com.jwell56.security.cloud.service.netstruct.entity.NetStruct;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwell56.security.cloud.service.netstruct.entity.dto.NetStructDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
public interface NetStructMapper extends BaseMapper<NetStruct> {
    List<NetStructDto> selectNetStructDtoList(@Param("map") Map map);
}
