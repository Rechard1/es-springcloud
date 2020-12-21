package com.jwell56.security.cloud.service.netstruct.entity.commons;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NetstructOrder {

	private LocalDateTime start;
	
	private LocalDateTime end;
	
	private Integer roleId;
	
	private Integer enterpriseId;
	
	private String areaLists;
	
	private String unitLists;
}
