package com.jwell56.security.cloud.service.netstruct.controller;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.netstruct.common.IPUtil;
import com.jwell56.security.cloud.service.netstruct.entity.Intranet;
import com.jwell56.security.cloud.service.netstruct.entity.User;
import com.jwell56.security.cloud.service.netstruct.entity.commons.PageParam;
import com.jwell56.security.cloud.service.netstruct.entity.vo.IntranetVo;
import com.jwell56.security.cloud.service.netstruct.service.IIntranetService;
import com.jwell56.security.cloud.service.netstruct.service.feign.UserComponent;
import com.jwell56.security.cloud.service.netstruct.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.netstruct.utils.ThreadLocalUtil;

import io.swagger.annotations.ApiOperation;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author wsg
 * @since 2019-11-01
 */
@RestController
@RequestMapping("/intranet")
public class IntranetController {
    @Autowired
    private IIntranetService iIntranetService;
    
    @Autowired
    private UserComponent userComponent;

    @ApiOperation("内网列表")
    @RequestMapping(value = "/inList", method = RequestMethod.GET)
    public ResultObject inList() {
        try {
            return ResultObject.data(iIntranetService.list(null));
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }

//    @ApiOperation("外网列表")
//    @RequestMapping(value = "/outList", method = RequestMethod.GET)
//    public ResultObject outList() {
//        try {
//            List<Intranet> intranetList = iIntranetService.list(null);
//            intranetList.add(new Intranet(0, ,"127.0.0.0", "127.255.255.255", null, null));
//            intranetList.add(new Intranet(0, "169.254.0.0", "169.254.255.255", null, null));
//            intranetList.add(new Intranet(0, "191.1.0.0", "191.1.255.255", null, null));
//            intranetList.add(new Intranet(0, "191.255.255.255", "191.255.255.255", null, null));
//            List<Map<String, Long>> outList = new ArrayList<>();
//            Map<String, Long> baseIp = new HashMap<>();
//            baseIp.put("startIp", IPUtil.ipToLong("0.0.0.1"));
//            baseIp.put("endIp", IPUtil.ipToLong("255.255.255.255"));
//            outList.add(baseIp);
//
//            for (Intranet intranet : intranetList) {
//                for (Map map : outList) {
//                    Long startIp = intranet.startIpNum();
//                    Long endIp = intranet.endIpNum();
//                    if ((Long) map.get("startIp") < startIp && (Long) map.get("endIp") > endIp) {
//                        outList.remove(map);
//
//                        Map<String, Long> beforeMap = new HashMap<>();
//                        beforeMap.put("startIp", (Long) map.get("startIp"));
//                        beforeMap.put("endIp", startIp - 1);
//                        outList.add(beforeMap);
//
//                        Map<String, Long> afterMap = new HashMap<>();
//                        afterMap.put("startIp", endIp + 1);
//                        afterMap.put("endIp", (Long) map.get("endIp"));
//                        outList.add(afterMap);
//
//                        break;
//                    }
//                }
//            }
//
//            for (Map map : outList) {
//                map.put("startIpNum", (Long) map.get("startIp"));
//                map.put("endIpNum", (Long) map.get("endIp"));
//                map.put("startIp", IPUtil.longToIP((Long) map.get("startIp")));
//                map.put("endIp", IPUtil.longToIP((Long) map.get("endIp")));
//            }
//
//            return ResultObject.data(outList);
//        } catch (Exception e) {
//            return ResultObject.exception(e);
//        }
//    }
//
    @ApiOperation("是否为内网")
    @RequestMapping(value = "/isIn", method = RequestMethod.GET)
    public ResultObject isIn(String ip) {
        try {
            Long ipn = IPUtil.ipToLong(ip);
            List<Intranet> intranetList = iIntranetService.listCache();
            boolean flag = false;
            for (Intranet intranet : intranetList) {
                flag = (intranet.startIpNum() <= ipn && intranet.endIpNum() >= ipn) || flag;
            }
            return ResultObject.data(flag);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    @ApiOperation("添加内网")
    @PostMapping("/add")
    public ResultObject add(@RequestBody Intranet intranet) {
    	ResultObject res = new ResultObject<>();
    	 //获取用户信息
      	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
      	QueryWrapper<Intranet> wrapper = new QueryWrapper<Intranet>();
      	wrapper.lambda().eq(Intranet :: getEnterpriseId, userInfo.getEnterpriseId());
      	List<Intranet> intranetList = iIntranetService.list(wrapper);
      	boolean flag = false;
        for (Intranet in : intranetList) {
            flag = (in.startIpNum() >= IPUtil.ipToLong(intranet.getStartIp()) && in.endIpNum() <= IPUtil.ipToLong(intranet.getEndIp())) || 
            	   (in.startIpNum() <= IPUtil.ipToLong(intranet.getStartIp()) && in.endIpNum() >= IPUtil.ipToLong(intranet.getStartIp())) || 
            	   (in.startIpNum() <= IPUtil.ipToLong(intranet.getEndIp()) && in.endIpNum() >= IPUtil.ipToLong(intranet.getEndIp())) || 
            	   (in.startIpNum() <= IPUtil.ipToLong(intranet.getStartIp()) && in.endIpNum() >= IPUtil.ipToLong(intranet.getStartIp())) || 
            	   flag;
            if(flag) { 
            	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                res.setSuccess(Boolean.FALSE);
                res.setMsg("ip段不能重复新增!");
                return res;
            }
        }
      	intranet.setUserId(userInfo.getUserId());
      	intranet.setEnterpriseId(userInfo.getEnterpriseId());
//		if(userInfo.getRoleType() == 1) {
//			intranet.setEnterpriseId(userInfo.getEnterpriseFlag());
//		}else {
//		}
		intranet.setEndipNum(IPUtil.ipToLong(intranet.getEndIp()));
		intranet.setStartipNum(IPUtil.ipToLong(intranet.getStartIp()));
		boolean b = iIntranetService.save(intranet);
        if (b) {
        	res.setCode(HttpServletResponse.SC_OK);
        	res.setSuccess(Boolean.TRUE);
        	res.setMsg("新增内网成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("新增内网失败");
        }
    	return res;
    }
    
    @ApiOperation("删除内网设备")
	@DeleteMapping("delete")
	public ResultObject delete(String intranetIds) {
		ResultObject res = new ResultObject();
		
		boolean b = iIntranetService.removeByIds(StringIdsUtil.listIds(intranetIds));
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除内网成功");
        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除内网失败");
        }
		
		return res;
	}
    
    @ApiOperation("修改内网")
	@PostMapping("update")
	public ResultObject update(@RequestBody Intranet intranet) {
		ResultObject res = new ResultObject();
		intranet.setEndipNum(IPUtil.ipToLong(intranet.getEndIp()));
		intranet.setStartipNum(IPUtil.ipToLong(intranet.getStartIp()));
		boolean b = iIntranetService.updateById(intranet);
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("修改内网成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("修改内网失败");
        }
		return res;
	}
    
	@ApiOperation("内网详情页面")
	@GetMapping("detail")
	public ResultObject detail(Integer intranetId) {
		ResultObject res = new ResultObject();
		Intranet intranet = iIntranetService.getById(intranetId);
		res.setData(intranet);
		res.setCode(HttpServletResponse.SC_OK);
        res.setSuccess(Boolean.TRUE);
		return res;
	}
	
	@ApiOperation("静态IP映射页面")
    @GetMapping("paging")
    public ResultObject paging(PageParam pageParam,String keyWord) {
    	
    	ResultObject res = new ResultObject<>();
    	QueryWrapper<Intranet> queryWrapper = new QueryWrapper<Intranet>();
    	
    	User userInfo = ThreadLocalUtil.getInstance().getUserInfo();
//		if(userInfo.getRoleType() == 1) {
//			queryWrapper.lambda().eq(Intranet :: getEnterpriseId, userInfo.getEnterpriseFlag());
//		}else {
//			queryWrapper.lambda().eq(Intranet :: getEnterpriseId, userInfo.getEnterpriseId());
//		}
		queryWrapper.lambda().eq(Intranet :: getEnterpriseId, userInfo.getEnterpriseId());
		if(!StringUtils.isEmpty(keyWord)) {
			if(!IPUtil.ipRegexMatches(keyWord)) {
				res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.setSuccess(Boolean.FALSE);
				res.setMsg("ip输入错误！");
				return res;
			}
			queryWrapper.lambda().le(Intranet :: getStartipNum, IPUtil.ipToLong(keyWord));
			queryWrapper.lambda().ge(Intranet :: getEndipNum, IPUtil.ipToLong(keyWord));
		}
		queryWrapper.lambda().orderByDesc(Intranet :: getCreateTime);
		IPage<Intranet> intranetPage = iIntranetService.page(pageParam.iPage(), queryWrapper);
		IPage<IntranetVo> resPage = new Page<IntranetVo>();
		List<IntranetVo> resList = new ArrayList<IntranetVo>();
		BeanUtils.copyProperties(intranetPage, resPage);
		for(Intranet intranet : intranetPage.getRecords()) {
			IntranetVo vo = new IntranetVo();
			BeanUtils.copyProperties(intranet, vo);
			vo.setUserName(userComponent.getUserName(intranet.getUserId()));
			resList.add(vo);
		}
		resPage.setRecords(resList);
		res.setData(resPage);
		res.setCode(HttpServletResponse.SC_OK);
    	return res;
    }
}
