package com.jwell56.security.cloud.service.netstruct.service.serviceImpl;

import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.netstruct.entity.Intranet;
import com.jwell56.security.cloud.service.netstruct.entity.NetStruct;
import com.jwell56.security.cloud.service.netstruct.mapper.IntranetMapper;
import com.jwell56.security.cloud.service.netstruct.service.IIntranetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
@Service
public class IntranetServiceImpl extends ServiceImpl<IntranetMapper, Intranet> implements IIntranetService {

    @Override
    public List<Intranet> listCache() {
        List<Intranet> intranetList = (List<Intranet>) CommonCachePool.getData("IntranetCache");
        if (intranetList == null) {
            intranetList = this.list(null);
            CommonCachePool.setData("IntranetCache", intranetList);
        }
        return intranetList;
    }
}
