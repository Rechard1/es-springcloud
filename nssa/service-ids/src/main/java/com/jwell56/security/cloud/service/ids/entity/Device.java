package com.jwell56.security.cloud.service.ids.entity;

import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/3
 */
@Data
public class Device {
    private String name;
    private String deviceType;
    private String ip;
    private Integer state;
}
