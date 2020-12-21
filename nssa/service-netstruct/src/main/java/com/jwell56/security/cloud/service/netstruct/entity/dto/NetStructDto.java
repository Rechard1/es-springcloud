package com.jwell56.security.cloud.service.netstruct.entity.dto;

import lombok.Data;

/**
 * @author wsg
 * @since 2019/11/1
 */
@Data
public class NetStructDto {
	private int netstructId;
    private Integer areaId;
    private Integer unitId;
    private Integer areaPid;
    private Integer unitPid;
    private String areaName;
    private String unitName;
    private String startIp;
    private String endIp;
}
