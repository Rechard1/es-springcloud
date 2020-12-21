package com.jwell56.security.cloud.service.role.service.serviceImpl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwell56.security.cloud.service.role.entity.Order;
import com.jwell56.security.cloud.service.role.mapper.OrderMapper;
import com.jwell56.security.cloud.service.role.service.IOrderService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

}
