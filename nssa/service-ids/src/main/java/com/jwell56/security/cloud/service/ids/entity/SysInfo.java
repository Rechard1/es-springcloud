package com.jwell56.security.cloud.service.ids.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wsg
 * @since 2020/12/3
 */
@Data
public class SysInfo {
    private Device device;
    private Map<String, Object> cpu;//totalUse
    private Map<String, Object> memory;
    private Map<String, Object> disk;

    public SysInfo() {
        this.device = new Device();
        this.cpu = new HashMap<>();
        this.memory = new HashMap<>();
        this.disk = new HashMap<>();
    }
}
