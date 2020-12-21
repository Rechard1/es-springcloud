package com.jwell56.security.cloud.service.netstruct.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.netstruct.entity.Topology;
import com.jwell56.security.cloud.service.netstruct.service.ITopologyService;

@Service
public class TopologyServiceImpl extends ServiceImpl<BaseMapper<Topology>, Topology> implements ITopologyService{

}
