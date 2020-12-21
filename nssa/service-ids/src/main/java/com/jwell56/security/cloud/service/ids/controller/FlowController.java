package com.jwell56.security.cloud.service.ids.controller;

import com.jwell56.security.cloud.service.ids.entity.Flow;
import com.jwell56.security.cloud.service.ids.service.FlowService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wsg
 * @since 2020/12/2
 */
@RestController
@Api(tags = "安全检测->流量日志")
@RequestMapping("/flow")
public class FlowController extends IdsBaseController<Flow> {
    @Autowired
    public FlowController(FlowService service) {
        this.service = service;
    }
}
