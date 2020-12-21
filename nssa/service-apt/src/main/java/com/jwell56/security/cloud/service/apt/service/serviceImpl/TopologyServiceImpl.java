package com.jwell56.security.cloud.service.apt.service.serviceImpl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.apt.entity.Topology;
import com.jwell56.security.cloud.service.apt.service.ITopologyService;

@Service
public class TopologyServiceImpl extends ServiceImpl<BaseMapper<Topology>, Topology> implements ITopologyService{

}
