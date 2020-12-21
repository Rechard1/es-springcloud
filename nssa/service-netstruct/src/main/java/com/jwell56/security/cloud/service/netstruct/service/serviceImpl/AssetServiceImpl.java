package com.jwell56.security.cloud.service.netstruct.service.serviceImpl;

import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.netstruct.entity.Area;
import com.jwell56.security.cloud.service.netstruct.entity.Asset;
import com.jwell56.security.cloud.service.netstruct.mapper.AssetMapper;
import com.jwell56.security.cloud.service.netstruct.service.IAssetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 资产表 服务实现类
 * </p>
 *
 * @author wsg
 * @since 2020-01-19
 */
@Service
public class AssetServiceImpl extends ServiceImpl<AssetMapper, Asset> implements IAssetService {
    @Override
    public List<Asset> listCache() {
        List<Asset> list = (List<Asset>) CommonCachePool.getData("assetListCache");
        if (list == null) {
            list = this.list(null);
            CommonCachePool.setData("assetListCache", list);
        }
        return list;
    }
}
