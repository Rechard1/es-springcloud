package com.jwell56.security.cloud.service.netstruct.service;

import com.jwell56.security.cloud.service.netstruct.entity.Intranet;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
public interface IIntranetService extends IService<Intranet> {
    List<Intranet> listCache();
}
