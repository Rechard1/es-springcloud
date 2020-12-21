package com.jwell56.security.cloud.service.netstruct.service;

import com.jwell56.security.cloud.service.netstruct.entity.Asset;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 资产表 服务类
 * </p>
 *
 * @author wsg
 * @since 2020-01-19
 */
public interface IAssetService extends IService<Asset> {
    List<Asset> listCache();
}
