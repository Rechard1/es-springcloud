package com.jwell56.security.cloud.service.role.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.common.cache.CommonCachePool;
import com.jwell56.security.cloud.service.role.entity.Enterprise;
import com.jwell56.security.cloud.service.role.entity.ModifyPasswordEntity;
import com.jwell56.security.cloud.service.role.entity.Module;
import com.jwell56.security.cloud.service.role.entity.Role;
import com.jwell56.security.cloud.service.role.entity.RoleModule;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.entity.TreeNode;
import com.jwell56.security.cloud.service.role.entity.dto.UserDto;
import com.jwell56.security.cloud.service.role.entity.vo.UserVo;
import com.jwell56.security.cloud.service.role.service.IEnterpriseService;
import com.jwell56.security.cloud.service.role.service.IModuleService;
import com.jwell56.security.cloud.service.role.service.IRoleModuleService;
import com.jwell56.security.cloud.service.role.service.IRoleService;
import com.jwell56.security.cloud.service.role.service.ISysUserService;
import com.jwell56.security.cloud.service.role.utils.JwtUtils;
import com.jwell56.security.cloud.service.role.utils.MD5Utils;
import com.jwell56.security.cloud.service.role.utils.RedisUtil;
import com.jwell56.security.cloud.service.role.utils.StringIdsUtil;
import com.jwell56.security.cloud.service.role.utils.ThreadLocalUtil;
import com.jwell56.security.cloud.service.role.utils.TreeNodeUtils;
import com.jwell56.security.cloud.service.role.utils.VerifyCodeUtils;
import com.jwell56.security.cloud.service.role.validated.AddUser;

import io.jsonwebtoken.Claims;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wsg
 * @since 2019/12/30
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private ISysUserService userService;
	
    @Autowired
    private IRoleService roleService;
    
    @Autowired
    private IEnterpriseService enterpriseService;
    
    @Autowired
    private IRoleModuleService iRoleModuleService;
    
    @Autowired
    private IModuleService iModuleService;
    
    @Autowired
    private RedisUtil redisUtil;

    @ApiOperation("获取用户id")
    @RequestMapping(value = "/userId", method = RequestMethod.GET)
    @ApiImplicitParam(paramType = "query", name = "token", value = "token", dataType = "String")
    public ResultObject login(String token) {
        try {
            if (token != null && !token.isEmpty()) {
                Claims c = JwtUtils.parseJWT(token);
                SysUser userObject = JSONObject.parseObject(c.get("userJsonString", String.class), new TypeReference<SysUser>() {
                });
                if (userObject != null) {
                    return ResultObject.data(userObject);
                }
            }
            return ResultObject.data(null);
        } catch (Exception e) {
            return ResultObject.exception(e);
        }
    }
    
    /**
     * 新增或修改用户
     *
     * @param user
     * @return
     */
    @Transactional
    @ApiOperation(value = "新增或修改用户", notes = "根据User对象新增或者修改用户")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject postUser(@Validated({AddUser.class}) @RequestBody SysUser user) {

        ResultObject resultObject = new ResultObject();

        if (user.getUsername() == null || StringUtils.isEmpty(user.getUsername())) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("用户名不能为空");
            return resultObject;
        }

        //判断用户名是否存在
        if (user.getUserId() == null) {
        	//获取用户信息
            SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
            QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.lambda().eq(SysUser::getUsername, user.getUsername());
            SysUser user1 = userService.getOne(queryWrapper);

            if (user1 != null) {
                resultObject.setSuccess(Boolean.FALSE);
                resultObject.setMsg("用户名已存在！");
                return resultObject;
            }
            if(user.getEnterpriseId() == null) {
            	user.setEnterpriseId(userInfo.getEnterpriseId());
            	user.setRoleType(3);
//            	if(userInfo.getRoleType() == 2) {
//                }else {
//                	user.setEnterpriseId(userInfo.getEnterpriseFlag());
//                }
            }
            else {
//            	Enterprise enterprise = enterpriseService.getById(user.getEnterpriseId());
//            	Role role = new Role();
//            	role.setCreatorId(userInfo.getUserId());
//            	role.setEnterpriseId(user.getEnterpriseId());
//            	role.setRoleDesc("企业管理员");
//            	role.setRoleName(enterprise.getEnterpriseName() + "企业管理员权限");
//            	role.setRoleType(2);
//            	roleService.save(role);
            	user.setRoleType(2);
//            	user.setRoleId(role.getRoleId());
            }
            user.setCreatorId(userInfo.getUserId());
        }


        //TODO 处理修改用户不能改密码问题
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(null);
//            resultObject.setSuccess(Boolean.FALSE);
//            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
//            resultObject.setMsg("未填写密码！");
//            return resultObject;
        } else {//密码验证
            boolean isLetter = Pattern.matches("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*? ]).*$", user.getPassword());
            if (!isLetter) {
                resultObject.setSuccess(Boolean.FALSE);
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
                resultObject.setMsg("密码至少8位，须包含字母、数字和以下特殊字符 !@#$%^&*?");
                return resultObject;
            }
        }

        if (user.getPassword() != null) {
            user.setPassword(MD5Utils.getMD5(user.getPassword()));//密码用md5加密
        }

        boolean b = userService.saveOrUpdate(user);

        if (b) {
            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setSuccess(Boolean.TRUE);
            if (user.getUserId() == null) {
                resultObject.setMsg("添加用户成功");
            } else {
                resultObject.setMsg("修改用户成功");
            }


        } else {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("操作失败，请联系管理员");
        }
        return resultObject;
    }
    
    /**
     * 用户分页列表
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @ApiOperation(value = "用户分页列表", notes = "获取用户分页列表")
    @RequestMapping(value = "/paging", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", name = "pageSize", value = "页长", dataType = "Integer"),
            @ApiImplicitParam(paramType = "query", name = "pageNum", value = "页码", dataType = "Integer"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject getUserList(Integer pageSize, Integer pageNum, String keyword, Integer roleType) {

        ResultObject resultObject = new ResultObject();

        try {
        	//获取用户信息
            SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
            QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>();
            if(!StringUtils.isEmpty(keyword)) {
            	userQueryWrapper.lambda().like(SysUser :: getUsername, keyword.trim()).or().
            	like(SysUser :: getPhoneNum, keyword.trim()).or().
            	like(SysUser :: getEmail, keyword.trim());
            }
//            if(userInfo.getRoleType() == 2) {
//            	userQueryWrapper.lambda().eq(SysUser :: getEnterpriseId, userInfo.getEnterpriseId());
//            }else {
//            	userQueryWrapper.lambda().eq(SysUser :: getEnterpriseId, userInfo.getEnterpriseFlag());
//            }
            userQueryWrapper.lambda().eq(SysUser :: getRoleType, roleType);
            if(roleType == 3) {
            	userQueryWrapper.lambda().eq(SysUser :: getEnterpriseId, userInfo.getEnterpriseId());
            }
            userQueryWrapper.lambda().orderByDesc(SysUser::getCreateTime);

            IPage iPage = new Page(pageNum, pageSize);
            IPage<SysUser> userIPage = userService.page(iPage, userQueryWrapper);

            IPage<UserVo> pageData = new Page();
            BeanUtils.copyProperties(userIPage, pageData);
            List<UserVo> userVoList = new ArrayList<>();
            pageData.setRecords(userVoList);

            if (!userIPage.getRecords().isEmpty()) {

                for (SysUser user : userIPage.getRecords()) {

                    user.setPassword("");//去掉密码

                    UserVo userVo = new UserVo();
                    BeanUtils.copyProperties(user, userVo);
                    Enterprise enterprise =enterpriseService.getById(user.getEnterpriseId());
                    userVo.setEnterpriseName(enterprise.getEnterpriseName());
                    if (user.getRoleId() != null) {
                        Role role = roleService.getById(user.getRoleId());
                        userVo.setRole(role);
                    }
                    userVoList.add(userVo);
                }
            }

            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setData(pageData);
        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return resultObject;
    }
    
    @ApiOperation("删除用户")
	@DeleteMapping("delete")
	public ResultObject delete(String userIds) {
		ResultObject res = new ResultObject();
		
		boolean b = false;
		b = userService.removeByIds(StringIdsUtil.listIds(userIds));
		if (b) {
			res.setCode(HttpServletResponse.SC_OK);
	        res.setSuccess(Boolean.TRUE);
	        res.setMsg("删除用户成功");

        } else {
        	res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("删除用户失败");
        }
		return res;
	}
    
    /**
     * 查询用户
     *
     * @param id
     * @return
     */
    @Transactional()
    @ApiOperation(value = "查询用户", notes = "查询用户详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "query", dataType = "Integer", name = "id", value = "用户ID", required = false),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject detail(@RequestParam(value = "userId") Integer userId) {

        ResultObject resultObject = new ResultObject();
        try {

            UserVo userVo = new UserVo();
            if (userId != null) {

                SysUser user = userService.getById(userId);
                BeanUtils.copyProperties(user, userVo);

                if (user.getRoleId() != null) {
                    Role role = roleService.getById(user.getRoleId());
                    userVo.setRole(role);
                }
            }

            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setData(userVo);

        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultObject.setMsg("查询失败");
            e.printStackTrace();
        }
        return resultObject;
    }
    
    @ApiOperation(value = "获取验证码", notes = "获取验证码")
    @RequestMapping(value = "/code", name = "name", method = RequestMethod.GET)
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
            }
    )
    public void getCode(HttpServletRequest request, HttpServletResponse response, String a) {

        ResultObject resultObject = new ResultObject();

        // 设置响应的类型格式为图片格式
        response.setContentType("image/jpeg");
        // 禁止图像缓存。
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession();
        //以秒为单位，即在没有活动30分钟后，session将失效
        session.setMaxInactiveInterval(30 * 60);

        //调用工具类生成验证码
        String verifyCode = VerifyCodeUtils.generateNumberVerifyCode(4);
        log.info("verifyCode----------" + verifyCode);
        //session.setAttribute("code", verifyCode);
        //保存到redis,并设置时长为10分钟
        redisUtil.set(a, verifyCode, 10 * 60);

        try {
            VerifyCodeUtils.outputImage(100, 38, response.getOutputStream(), verifyCode);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @ApiOperation(value = "用户登录", notes = "用户输入账号密码登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "user", value = "用户详细实体user", required = true, dataType = "UserDto"),
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResultObject login(HttpServletRequest request,
                              HttpServletResponse response,
                              @RequestBody UserDto user) throws ServletException, IOException {

        ResultObject resultObject = new ResultObject();

//        String authHeader = request.getHeader("Authorization");
//        if (!StringUtils.isEmpty(authHeader)) {
//
//            if (redisUtil.hasKey(authHeader) && redisUtil.getExpire(authHeader) > 0) {
//
//                Map map = new HashMap<String, Object>();
//                map.put("token", authHeader);
//
//                String authHeaderString = (String) redisUtil.get(authHeader);
//                User userLogin = JSONObject.parseObject(authHeaderString, User.class);
//                map.put("user", userLogin);
//
//                Object roleModuleListString = redisUtil.get("userModuleList:"+userLogin.getId());
//                if(roleModuleListString!=null){
//                    List<TreeNode> roleModuleList = JSONObject.parseObject((String)roleModuleListString, List.class);
//                    map.put("userModuleList", roleModuleList);
//                }
//
//                resultObject.setData(map);
//                resultObject.setCode(HttpServletResponse.SC_OK);
//                resultObject.setMsg("登录成功");
//                resultObject.setSuccess(Boolean.TRUE);
//
//                return resultObject;
//            }
//        }

        String code = user.getCode();

        if (code == null || StringUtils.isEmpty(code)) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setMsg("验证码不能为空");
            return resultObject;
        }

//        boolean b = ((String) redisUtil.get(user.getFlag())).equals(code);
//        if(!code.equals("9999")) {
//        	
//        }
        
        if (!redisUtil.get(user.getFlag()).equals(code)) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setMsg("验证码错误！");
            return resultObject;
        }

        Long time = redisUtil.getExpire(user.getFlag());
        if (time == 0) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setMsg("验证码过期！");
            return resultObject;
        }

        if (user.getUsername() == "" || user.getUsername() == null
                || user.getPassword() == "" || user.getPassword() == null) {
            resultObject.setCode(HttpServletResponse.SC_NOT_FOUND);
            resultObject.setMsg("用户名和账号不能为空");
            return resultObject;
        }

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, user.getUsername())
                .and(e -> e.eq(SysUser::getPassword, MD5Utils.getMD5(user.getPassword())));

        SysUser userLogin = userService.getOne(queryWrapper);

        if (userLogin != null) {

            //生成token字符串
            String jwtToken = JwtUtils.createJWT("jwt",
                    "{id:10086,name:Ronnie}", userLogin);

            response.addHeader("Authorization", jwtToken);

//            HttpSession session = request.getSession();
//            session.setAttribute("user",userLogin);
            //保存用户到redis,保存时长是30分钟
            redisUtil.set(jwtToken, JSONObject.toJSONString(userLogin), 30 * 60);

            Map map = new HashMap<String, Object>();
            map.put("token", jwtToken);
            map.put("user", userLogin);

            //获取用户配置的角色访问模块
            if (userLogin.getRoleType() != null) {
            	List<Module> roleModuleVoList = new ArrayList<Module>();
            	if(userLogin.getRoleType() == 3) {
//            		List<Module> roleModuleList = iRoleModuleService.getRoleModuleVoList(userLogin.getRoleId());
            		QueryWrapper<RoleModule> wrapper = new QueryWrapper<RoleModule>();
            		wrapper.lambda().eq(RoleModule :: getRoleId, userLogin.getRoleId());
            		List<RoleModule> roleModuleList = iRoleModuleService.list(wrapper);
            		Set<Integer> set = new HashSet<Integer>();
            		for(RoleModule roleModule : roleModuleList) {
            			getParentId(roleModule.getModuleId(), set);
            		}
            		List<Integer> list = new ArrayList<Integer>(set);
            		QueryWrapper<Module> wrapper1 = new QueryWrapper<Module>();
            		wrapper1.lambda().in(Module :: getModuleId, list);
            		
            		roleModuleVoList = iModuleService.list(wrapper1);
            	}else if(userLogin.getRoleType() == 2) {
            		QueryWrapper<Module> wrapper = new QueryWrapper<Module>();
            		wrapper.lambda().ne(Module :: getModuleName, "企业管理").ne(Module :: getPId, 28);
            		roleModuleVoList = iModuleService.list(wrapper);
            	}else {
            		QueryWrapper<Module> wrapper = new QueryWrapper<Module>();
            		roleModuleVoList = iModuleService.list(wrapper);
            	}
            	
                List<TreeNode> treeNodeList = new LinkedList<>();
                for (Module roleModuleVo : roleModuleVoList) {

                	if(roleModuleVo.getPId() == 0) {
                		TreeNode treeNode = new TreeNode(roleModuleVo.getModuleId(),
                                roleModuleVo.getPId(),
                                roleModuleVo.getLevel(),
                                roleModuleVo.getModuleName(),
                                roleModuleVo.getBasePath(),roleModuleVo.getNav());
                        treeNodeList.add(treeNode);
                	}else {
                		TreeNode treeNode = new TreeNode(roleModuleVo.getModuleId(),
                                roleModuleVo.getPId(),
                                roleModuleVo.getLevel(),
                                roleModuleVo.getModuleName(),
                                roleModuleVo.getBasePath());
                        treeNodeList.add(treeNode);
                	}
                    
                }

                //加入所有新增，修改和详情的最底层菜单
//                QueryWrapper<Module> moduleQueryWrapper = new QueryWrapper<>();
//                moduleQueryWrapper.lambda().eq(Module::getModuleName, "***")
//                        .or().eq(Module::getLevel, 4);
//                List<Module> moduleList = iModuleService.list(moduleQueryWrapper);
//                List<TreeNode> treeNodeListTemp = new LinkedList<>();
//                for (Module module : moduleList) {
//
//                	if(module.getPId() == 0) {
//                		TreeNode treeNode = new TreeNode(module.getModuleId(),
//                                module.getPId(),
//                                module.getLevel(),
//                                module.getModuleName(),
//                                module.getBasePath(),module.getNav());
//                        treeNodeListTemp.add(treeNode);
//                	}else {
//                		TreeNode treeNode = new TreeNode(module.getModuleId(),
//                                module.getPId(),
//                                module.getLevel(),
//                                module.getModuleName(),
//                                module.getBasePath());
//                        treeNodeListTemp.add(treeNode);
//                	}
//                    
//                }
//                treeNodeList.addAll(treeNodeListTemp);

                //返回菜单模块
                map.put("userModuleList", TreeNodeUtils.getTreeList(treeNodeList, 0));
                redisUtil.set("userModuleList:" + userLogin.getUserId(), JSONObject.toJSONString(TreeNodeUtils.getTreeList(treeNodeList, 0)));

            } else {
                resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
                resultObject.setMsg("用户未设置角色，请先设置正确角色！");
                return resultObject;
            }

            //====================保存登录日志======================
//            OperationLog operationLog = new OperationLog();
//
//            operationLog.setRequestUrl(request.getRequestURL().toString());
//            operationLog.setRemoteAddr(request.getRemoteAddr());
//            operationLog.setHttpMethod(request.getMethod());
//            operationLog.setClassMethod(this.getClass().getName() + "." +
//                    Thread.currentThread().getStackTrace()[1].getMethodName());//1级堆栈
//
//            operationLog.setHandleModule("权限管理模块");
//            operationLog.setHandleUser(userLogin.getUsername());
//            operationLog.setHandleType("用户登录");
//            operationLog.setHandleDes("登录成功");
//            iOperationLogService.saveOrUpdate(operationLog);
            //=====================================================

            resultObject.setData(map);
            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setMsg("登录成功");
            resultObject.setSuccess(Boolean.TRUE);
        } else {
            resultObject.setMsg("登录失败,账号或者密码错误");
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setCode(HttpServletResponse.SC_NOT_FOUND);
        }
        return resultObject;
    }
    
    @GetMapping("/getUserById")
    public ResultObject getUserById(@RequestParam("userId") Integer userId) {
    	return ResultObject.data(userService.getById(userId));
    }

    @ApiOperation(value = "用户列表", notes = "用户列表")
    @RequestMapping(value = "/all", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject all() {

        ResultObject resultObject = new ResultObject();

        try {
            //获取用户信息
            SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
            QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.lambda().eq(SysUser :: getEnterpriseId, userInfo.getEnterpriseId());

            userQueryWrapper.lambda().orderByDesc(SysUser::getCreateTime);

            List<SysUser> list = userService.list(userQueryWrapper);

            for(SysUser sysUser : list){
                sysUser.setPassword("");//去掉密码
            }

            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setData(list);
        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
        return resultObject;
    }
    
    @ApiOperation(value = "注销登录", notes = "用户注销登录退出")
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "Authorization", value = "token", required = false)
    }
    )
    public ResultObject logout(HttpServletRequest req, HttpServletResponse resp) {

        ResultObject resultObject = new ResultObject();

        try {
            String authHeader = req.getHeader("Authorization");
            Boolean b = redisUtil.expire(authHeader, 0);
            if (b) {
                resultObject.setMsg("退出成功!");
                resultObject.setCode(HttpServletResponse.SC_OK);
            }
        } catch (Exception e) {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultObject.setMsg("退出失败，请联系管理员");
            e.printStackTrace();
        }
        return resultObject;

    }
    
    @ApiOperation(value = "修改密码", notes = "修改密码")
    @PostMapping("/modifyPassword")
    public ResultObject modifyPassword(@RequestBody ModifyPasswordEntity modifyPassword) {
    	ResultObject res = new ResultObject<>();
    	//获取用户信息
        SysUser userInfo = ThreadLocalUtil.getInstance().getUserInfo();
        if(userInfo.getPassword().equals(MD5Utils.getMD5(modifyPassword.getOldPassword()))) {
        	boolean isLetter = Pattern.matches("^.*(?=.{8,})(?=.*\\d)(?=.*[a-zA-Z])(?=.*[!@#$%^&*? ]).*$", modifyPassword.getNewPassword());
            if (!isLetter) {
                res.setSuccess(Boolean.FALSE);
                res.setCode(HttpServletResponse.SC_BAD_REQUEST);
                res.setMsg("密码至少8位，须包含字母、数字和以下特殊字符 !@#$%^&*?");
                return res;
            }
        	if(modifyPassword.getNewPassword().equals(modifyPassword.getConfirmPassword())) {
        		userInfo.setPassword(MD5Utils.getMD5(modifyPassword.getNewPassword()));//密码用md5加密
        		userService.updateById(userInfo);
        		res.setCode(HttpServletResponse.SC_OK);
                res.setMsg("修改密码成功");
            	return res;
        	}else {
        		res.setSuccess(Boolean.FALSE);
                res.setCode(HttpServletResponse.SC_BAD_REQUEST);
                res.setMsg("确认密码与新密码不匹配，修改密码失败");
                return res;
        	}
        }
        
        res.setSuccess(Boolean.FALSE);
        res.setCode(HttpServletResponse.SC_BAD_REQUEST);
        res.setMsg("原密码错误，修改密码失败");
    	return res;
    }
    
    private void getParentId(Integer moduleId, Set<Integer> moduleIdSet){
    	String cacheKey = "module_by_id"+ moduleId;
    	Module module = (Module) CommonCachePool.getData(cacheKey);
    	if(module == null) {
    		module = iModuleService.getById(moduleId);
    	}
    	moduleIdSet.add(moduleId);
    	if(module.getPId() == 0 || module.getPId() == null) {
    		return;
    	}
    	getParentId(module.getPId(), moduleIdSet);
    }
}
