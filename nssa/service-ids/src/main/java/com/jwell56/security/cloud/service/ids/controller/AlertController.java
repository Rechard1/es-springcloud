package com.jwell56.security.cloud.service.ids.controller;

import com.jwell56.security.cloud.service.ids.entity.Alert;
import com.jwell56.security.cloud.service.ids.service.AlertService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wsg
 * @since 2020/12/2
 */
@RestController
@Api(tags = "安全检测->告警日志")
@RequestMapping("/alert")
public class AlertController extends IdsBaseController<Alert> {
    @Autowired
    public AlertController(AlertService service) {
        this.service = service;
    }
}
