package com.jwell56.security.cloud.service.ids.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.yaml.snakeyaml.Yaml;

import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.ids.common.CommandExecutor;
import com.jwell56.security.cloud.service.ids.common.FileUtils;
import com.jwell56.security.cloud.service.ids.entity.ThreadParam;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api("ids探针配置")
@RestController
@RequestMapping("/setting/")
public class SetingController {

	@ApiOperation("系统配置")
	@GetMapping("system")
	public ResultObject systemSetting(String ip, String netmask ,String gateway) {
		ResultObject res = new ResultObject();
		File configFile = new File("/opt/webstored/product_info/system_ip_config.sh");
		if(!configFile.exists()) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("不存在脚本文件！");
			return res;
		}
		
		if (!isboolIp(ip)) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("请输入正确的ip!");
			return res;
		}
		String command = "sh system_ip_config.sh " + ip + " " + netmask + " " +gateway;
		CommandExecutor commandExecutor = new CommandExecutor(command,"/opt/webstored/product_info");
		String result = commandExecutor.execute();
		if (result.equals("0")) {
			res.setData(result);
			res.setCode(HttpServletResponse.SC_OK);
			res.setSuccess(Boolean.TRUE);
		} else {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("配置失败！");
		}
		return res;
	}

	@ApiOperation("进程配置页面")
	@GetMapping("thread/paging")
	public ResultObject threadPaging() {
		ResultObject res = new ResultObject();
		Map<String, Object> resMap = new HashMap<String, Object>();
		Map<String, Object> yamlMap = new LinkedHashMap<String, Object>();
		Yaml yaml = new Yaml();
		File file = new File("/opt/webstored/core/suricata.yaml");
		try {
			yamlMap = (Map) yaml.load(new FileInputStream(file));
			List<Map<String, Object>> yamlMap1 = (List<Map<String, Object>>) yamlMap.get("af-packet");
			Map<String, Object> yamlMap2 = (Map<String, Object>) yamlMap.get("vars");
			Map<String, Object> yamlMap3 = (Map<String, Object>) yamlMap2.get("address-groups");
			resMap.put("dir", yamlMap.get("default-log-dir"));
			resMap.put("threads", yamlMap1.get(0).get("threads"));
			resMap.put("interface", yamlMap1.get(0).get("interface"));
			resMap.put("HOME_NET", yamlMap3.get("HOME_NET"));
		} catch (Exception e) {
			res.setData(null);
			res.setCode(HttpServletResponse.SC_OK);
			res.setSuccess(Boolean.TRUE);
			res.setMsg("获取配置文件失败！");
			return res;
		}
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
		res.setSuccess(Boolean.TRUE);
		return res;
	}

	@ApiOperation("修改进程下发配置")
	@PostMapping("thread/commad")
	public ResultObject threadCommand(@RequestBody ThreadParam param) {
		ResultObject res = new ResultObject();
		File configFile = new File("/opt/webstored/core/core_config_update.sh");
		if(!configFile.exists()) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("不存在脚本文件！");
			return res;
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("sh core_config_update.sh ");
		try {
			sb.append("\"");
			if (!StringUtils.isEmpty(param.getDir())) {
				sb.append(param.getDir());
				sb.append(";");
			}
			if (!StringUtils.isEmpty(param.getThreads())) {
				sb.append(param.getThreads());
				sb.append(";");
			}
			if (!StringUtils.isEmpty(param.getThreadInterface())) {
				sb.append(param.getThreadInterface());
				sb.append(";");
			}
			if (!StringUtils.isEmpty(param.getHOME_NET())) {
				sb.append(param.getHOME_NET());
				sb.append(";");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append("\"");
			CommandExecutor commandExecutor = new CommandExecutor(sb.toString(), "/opt/webstored/core");

			// 执行命令
			String result = commandExecutor.execute();
			if (result.equals("0")) {
				res.setData(result);
				res.setCode(HttpServletResponse.SC_OK);
				res.setSuccess(Boolean.TRUE);
			} else {
				res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setSuccess(Boolean.FALSE);
				res.setMsg("配置失败！");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}

	@ApiOperation("rsyslog配置")
	@GetMapping("rsyslog")
	public ResultObject rsyslogSetting(String ipPort, String status) {
		ResultObject res = new ResultObject();
		
		File configFile = new File("/opt/webstored/rsyslog_config/rsyslog_config.sh");
		if(!configFile.exists()) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("不存在脚本文件！");
			return res;
		}
		
		if (!isboolIpPort(ipPort)) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("请输入正确的ip端口!");
			return res;
		}
		String command = "sh rsyslog_config.sh " + status + " " + ipPort;
		CommandExecutor commandExecutor = new CommandExecutor(command, "/opt/webstored/rsyslog_config");
		String result = commandExecutor.execute();
		if (result.equals("0")) {
			res.setData(result);
			res.setCode(HttpServletResponse.SC_OK);
			res.setSuccess(Boolean.TRUE);
		} else {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("配置失败！");
		}
		return res;
	}

	@ApiOperation("约定配置")
	@GetMapping("license/paging")
	public ResultObject licensePaging() {
		ResultObject res = new ResultObject();
		File file = new File("/opt/webstored/license/fingerprint.txt");
		File file1 = new File("/opt/webstored/license/license_auth_info.txt");
		String result = "";
		String time = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));// 构造一个BufferedReader类来读取文件
			String s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				result = result + "\n" + s;
			}
			br = new BufferedReader(new FileReader(file1));
			s = null;
			while ((s = br.readLine()) != null) {// 使用readLine方法，一次读一行
				time = time + "\n" + s;
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String, Object> resMap = new HashMap<String, Object>();
		resMap.put("result", result);
		resMap.put("time", time);
		res.setData(resMap);
		res.setCode(HttpServletResponse.SC_OK);
		res.setSuccess(Boolean.TRUE);
		return res;
	}
	
	@ApiOperation("约定配置上传")
	@PostMapping("license/upload")
	public ResultObject licenseUpload(MultipartFile file) {
		ResultObject res = new ResultObject();
		File licenseFile = new File("/opt/webstored/license/license_update.sh");
		if(!licenseFile.exists()) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("不存在脚本文件！");
			return res;
		}
		
		String[] str = file.getOriginalFilename().split("\\.");
		String newFilename = str[0]+ new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + "."+ str[1];
		
		FileUtils.upload(file, newFilename,"/opt/webstored/license");
		
		String command = "sh license_update.sh " + "/opt/webstored/license/" +newFilename;
		CommandExecutor commandExecutor = new CommandExecutor(command, "/opt/webstored/license");
		//执行命令
		String result = commandExecutor.execute();
		if (result.equals("0")) {
			res.setData(result);
			res.setCode(HttpServletResponse.SC_OK);
			res.setSuccess(Boolean.TRUE);
		} else {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("配置失败！");
		}
		return res;
	}
	
	@ApiOperation("升级包上传")
	@PostMapping("upgrade/upload")
	public ResultObject upgradeUpload(MultipartFile file) {
		ResultObject res = new ResultObject();
		
		File upgradeFile = new File("/opt/webstored/product_info/system_upgrade.sh");
		if(!upgradeFile.exists()) {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("不存在脚本文件！");
			return res;
		}
		
		String[] str = file.getOriginalFilename().split("\\.");
		String newFilename = str[0]+ new SimpleDateFormat("yyMMddHHmmss").format(new Date()) + "."+ str[1];
		FileUtils.upload(file, newFilename,"/opt/webstored/product_info");
		
		String command = "sh system_upgrade.sh " + "/opt/webstored/product_info/" +newFilename;
		CommandExecutor commandExecutor = new CommandExecutor(command, "/opt/webstored/product_info");
		//执行命令
		String result = commandExecutor.execute();
		if (result.equals("0")) {
			res.setData(result);
			res.setCode(HttpServletResponse.SC_OK);
			res.setSuccess(Boolean.TRUE);
		} else {
			res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			res.setSuccess(Boolean.FALSE);
			res.setMsg("配置失败！");
		}
		return res;
	}

	// 判断是否为合法IP
	private boolean isboolIp(String ipAddress) {
		String ip = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipAddress);
		return matcher.matches();
	}

	// 判断是否为合法ip:port
	private boolean isboolIpPort(String ipPort) {
		String ip = "^(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5]):([0-9]|[1-9]\\d|[1-9]\\d{2}|[1-9]\\d{3}|[1-5]\\d{4}|6[0-4]\\d{3}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])$";
		Pattern pattern = Pattern.compile(ip);
		Matcher matcher = pattern.matcher(ipPort);
		return matcher.matches();
	}
}
