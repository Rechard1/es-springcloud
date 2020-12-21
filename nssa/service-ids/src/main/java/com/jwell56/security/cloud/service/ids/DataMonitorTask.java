package com.jwell56.security.cloud.service.ids;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.jwell56.security.cloud.common.util.ShellUtil;
import com.jwell56.security.cloud.service.ids.entity.DataMonitor;
import com.jwell56.security.cloud.service.ids.service.IDataMonitorService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Order(1)
@Component
public class DataMonitorTask implements ApplicationRunner{

	@Autowired
	private IDataMonitorService dataMonitorService;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		// TODO Auto-generated method stub
		while(true) {
			List<DataMonitor> dataMonitors = new ArrayList<DataMonitor>();
			String data = ShellUtil.ExecCommand("cat /proc/net/dev");
			LocalDateTime createTime = LocalDateTime.now();
			String[] dataMonitorLines = data.split("\n");
			for (String line : dataMonitorLines) {
				DataMonitor dataMonitor = new DataMonitor();
				line = line.trim();
				String[] items = line.split(" +");
				if (items.length < 17 || items[2].contains("bytes"))
					continue;
				
				long re = Long.parseLong(items[1]);
				long tran = Long.parseLong(items[9]);
				
				
				dataMonitor.setReceive(re / 1048576);
				dataMonitor.setTransmit(tran /1048576);
				dataMonitor.setCreateTime(createTime);
				dataMonitor.setInterfaceName(items[0].substring(0, items[0].length()-1));
				dataMonitors.add(dataMonitor);
			}
			dataMonitorService.saveBatch(dataMonitors);
			log.info("流量数据入库成功");
			Thread.sleep(1000 * 60);
		}
		
	}

}
