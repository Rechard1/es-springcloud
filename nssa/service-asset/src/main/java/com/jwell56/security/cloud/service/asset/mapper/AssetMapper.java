package com.jwell56.security.cloud.service.asset.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwell56.security.cloud.service.asset.entity.Asset;

public interface AssetMapper extends BaseMapper<Asset>{

	boolean important(@Param("assetIds") List<Integer> assetIds);
}
