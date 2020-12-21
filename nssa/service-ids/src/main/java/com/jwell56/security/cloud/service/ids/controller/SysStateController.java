package com.jwell56.security.cloud.service.ids.controller;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.util.ShellUtil;
import com.jwell56.security.cloud.service.ids.entity.SysInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author wsg
 * @since 2020/12/3
 */
@RestController
@Api("系统状态接口")
@RequestMapping("/sys-state")
public class SysStateController {
	@Value("${spring.redis.host}")
	private String localIp;

	@ApiOperation("系统状态")
	@RequestMapping(value = "/info", method = RequestMethod.GET)
	public ResultObject<SysInfo> info() {
		SysInfo sysInfo = new SysInfo();

		sysInfo.getDevice().setDeviceType("安全探针");
		sysInfo.getDevice().setName("网络威胁感知系统");
		sysInfo.getDevice().setIp(getHostIp());
		sysInfo.getDevice().setState(1);
		String cpu = ShellUtil.ExecCommand("mpstat -P ALL 1 1");
//        System.out.println(cpu);
		String[] cpuLines1 = cpu.split("\n");
		List<String> ll = new ArrayList(Arrays.asList(cpuLines1));
		Iterator<String> iterator = ll.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().contains("Average")) {
				iterator.remove();
			}
		}
		String[] cpuLines = (String[]) ll.toArray(new String[ll.size()]);
		for (String line : cpuLines) {
			String[] items = line.split(" +");
			if (items.length < 12 || items[2].equals("CPU"))
				continue;
			Double per = 100.00 - Double.parseDouble(items[12]);
			if (items[2].equals("all")) {
				sysInfo.getCpu().put("totalUse", per);
			} else {
				sysInfo.getCpu().put("cpu" + items[2], per);
			}
		}

		String mem = ShellUtil.ExecCommand("free");
//        System.out.println(mem);
		String[] memLines = mem.split("\n");
		for (String line : memLines) {
			String[] items = line.split(" +");
			if (items.length < 4)
				continue;
			if (items[0].startsWith("Mem")) {
				sysInfo.getMemory().put("phy_totalUser", Integer.parseInt(items[2]) * 100 / Integer.parseInt(items[1]));
				sysInfo.getMemory().put("phy_total", Double.parseDouble(items[1]) / 1024 / 1024);
				sysInfo.getMemory().put("phy_used", Integer.parseInt(items[2]) / 1024 / 1024);
				sysInfo.getMemory().put("phy_lave", Integer.parseInt(items[3]) / 1024 / 1024);
			} else if (items[0].startsWith("Swap")) {
				sysInfo.getMemory().put("vir_totalUser", Integer.parseInt(items[2]) * 100 / Integer.parseInt(items[1]));
				sysInfo.getMemory().put("vir_total", Integer.parseInt(items[1]) / 1024 / 1024);
				sysInfo.getMemory().put("vir_used", Integer.parseInt(items[2]) / 1024 / 1024);
				sysInfo.getMemory().put("vir_lave", Integer.parseInt(items[3]) / 1024 / 1024);
			}
		}

		String disk = ShellUtil.ExecCommand("df");
//        System.out.println(disk);
		String[] diskLines = disk.split("\n");
		double diskTotal = 0.0;
		double diskUsed = 0.0;
		for (String line : diskLines) {
			String[] items = line.split(" +");
			if (items.length < 6 || items[0].startsWith("Filesystem"))
				continue;
			if (items[5].equals("/") || items[5].equals("/data") || items[5].equals("/home")) {
				Double total = Double.parseDouble(items[1]) / 1024 / 1024;
				Double used = Double.parseDouble(items[2]) / 1024 / 1024;
				diskTotal += total;
				diskUsed += used;
				sysInfo.getDisk().put(items[5], new HashMap<String, Object>() {
					{
						this.put("total", total);
						this.put("used", used);
					}
				});
			}

		}

//		sysInfo.getDisk().put("totalUsedPer", diskUsed * 100 / diskTotal);

		return ResultObject.data(sysInfo);
	}

	private String getHostIp() {
		String ip = "";
		try {
			Enumeration e1 = (Enumeration) NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface ni = (NetworkInterface) e1.nextElement();
				if (!ni.getName().equals("em2")) {
					continue;
				} else {
					Enumeration e2 = ni.getInetAddresses();
					while (e2.hasMoreElements()) {
						InetAddress ia = (InetAddress) e2.nextElement();
						if (ia instanceof Inet6Address)
							continue;
						ip = ia.getHostAddress();
					}
					break;
				}
			}
		} catch (SocketException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		return ip;
	}
}
