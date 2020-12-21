package com.jwell56.security.cloud.service.netstruct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("sys_topology")
public class Topology {

	@TableId(value = "topology_id",type= IdType.AUTO)
    private Integer topologyId;
	
	private Integer pid;
	
	private String keyName;
	
	private String name;
	
	private Integer areaId;
	
	private Integer unitId;
	
	private String startIp;
	
	private String endIp;
	
	private Integer access;
	
	private Integer enterpriseId;
}
