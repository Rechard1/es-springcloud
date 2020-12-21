package com.jwell56.security.cloud.service.netstruct.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class StringIdsUtil {

	public static List<Integer> listIds(String id){
		List<Integer> listIds = new ArrayList<Integer>();
		if(StringUtils.isEmpty(id)) return listIds;
		String[] ids = id.split(",");
		
		for(String stringId : ids) {
			listIds.add(Integer.parseInt(stringId));
		}
		return listIds;
	}
	
	public static String StringIds(List<Integer> ids){
		if(ids.isEmpty() || ids == null) return "";
		StringBuffer sb = new StringBuffer();
		for(Integer id : ids) {
			sb.append(id).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
}
