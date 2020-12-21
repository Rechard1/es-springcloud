package com.jwell56.security.cloud.service.ids.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.ids.entity.DataMonitor;
import com.jwell56.security.cloud.service.ids.service.IDataMonitorService;

@RestController
@RequestMapping("/flow/")
public class DataMonitorController {

	@Autowired
	private IDataMonitorService dataMonitorService;
	
	@GetMapping("/getEchars")
	public ResultObject getEchars() {
		ResultObject res = new ResultObject();
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String string = new SimpleDateFormat("yyyy-MM-dd").format(new Date()).toString();
		QueryWrapper<DataMonitor> wrapper1 = new QueryWrapper<>();
		List<DataMonitor> dataMonitors = new ArrayList<DataMonitor>();
		wrapper1.lambda().gt(DataMonitor :: getCreateTime, LocalDateTime.parse(string +" 00:00:00", df)).groupBy(DataMonitor :: getInterfaceName);
		List<DataMonitor> names = dataMonitorService.list(wrapper1);
        
		try {
            Map<String,Object> map = new LinkedHashMap<>();
            List<String> list = new LinkedList<>();
            list.add("00:00:00");
            list.add("03:00:00");
            list.add("06:00:00");
            list.add("09:00:00");
            list.add("12:00:00");
            list.add("15:00:00");
            list.add("18:00:00");
            list.add("21:00:00");
            list.add("24:00:00");
            for(int i=0; i<list.size() ;i++){
            	if(i+1 == list.size()) break;
            	List<DataMonitor> lists = new ArrayList<DataMonitor>();
            	for(DataMonitor m : names) {
            		DataMonitor dataMonitor = new DataMonitor();
            		dataMonitor.setInterfaceName(m.getInterfaceName());
            		dataMonitor.setTransmit(0);
            		dataMonitor.setReceive(0);
            		lists.add(dataMonitor);
            	}
               
                LocalDateTime start = LocalDateTime.parse(string +" "+ list.get(i), df);
                LocalDateTime end = LocalDateTime.parse(string +" "+ list.get(i+1), df);
                
                List<DataMonitor> resList = getFlowData(start, end, names);
                if(resList.isEmpty()) {
                	map.put(list.get(i),lists);
                }else {
                	map.put(list.get(i),resList);
                }
            }

            res.setData(map);
            res.setCode(HttpServletResponse.SC_OK);

        } catch (Exception e) {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
		
		return res;
	}
	
	private List<DataMonitor> getFlowData(LocalDateTime start, LocalDateTime end, List<DataMonitor> names) {
		
//		QueryWrapper<DataMonitor> wrapper1 = new QueryWrapper<>();
		List<DataMonitor> res = new ArrayList<DataMonitor>();
//		wrapper1.lambda().between(DataMonitor :: getCreateTime, start, end).groupBy(DataMonitor :: getInterfaceName);
//		List<DataMonitor> names = dataMonitorService.list(wrapper1);
		for(DataMonitor name : names) {
			QueryWrapper<DataMonitor> wrapper = new QueryWrapper<>();
			wrapper.lambda().eq(DataMonitor :: getInterfaceName, name.getInterfaceName());
			wrapper.lambda().between(DataMonitor :: getCreateTime, start, end).orderByDesc(DataMonitor :: getCreateTime);
			List<DataMonitor> dataMonitors = dataMonitorService.list(wrapper);
			DataMonitor dataMonitor = new DataMonitor();
			if(dataMonitors.isEmpty()) {
				continue;
			}
			dataMonitor.setInterfaceName(name.getInterfaceName());
			dataMonitor.setReceive(dataMonitors.get(0).getReceive() - dataMonitors.get(dataMonitors.size() - 1).getReceive());
			dataMonitor.setTransmit(dataMonitors.get(0).getTransmit() - dataMonitors.get(dataMonitors.size() - 1).getTransmit());
			res.add(dataMonitor);
		}
		
		return res;
	}
	
}
