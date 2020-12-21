package com.jwell56.security.cloud.service.role.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.jwell56.security.cloud.common.ResultObject;
import com.jwell56.security.cloud.service.role.entity.LoopHoleVx;
import com.jwell56.security.cloud.service.role.entity.Order;
import com.jwell56.security.cloud.service.role.entity.SysUser;
import com.jwell56.security.cloud.service.role.entity.SysUserVx;
import com.jwell56.security.cloud.service.role.entity.vo.*;
import com.jwell56.security.cloud.service.role.service.ILoopHoleVxService;
import com.jwell56.security.cloud.service.role.service.IOrderService;
import com.jwell56.security.cloud.service.role.service.ISysUserService;
import com.jwell56.security.cloud.service.role.service.ISysUserVxService;
import com.jwell56.security.cloud.service.role.utils.FileUploadUtils;
import com.jwell56.security.cloud.service.role.utils.MD5Utils;
import com.jwell56.security.cloud.service.role.utils.VXHttpRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.xmlbeans.impl.util.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/vx")
public class VXController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysUserVxService sysUserVxService;

    @Autowired
    private IOrderService orderService;

    @Autowired
    private ILoopHoleVxService iLoopHoleVxService;

    //小程序的appid
    private static final String appId = "wx9aa24dedf8019fcd";
    // 小程序的secret
    private static final String appsecret = "721b8925ad96548dfac0a36735c635e0";

//    private static final String templateId = "lNYItu_BpH78SXkloMOYsV1ihSiP-f9spmGgkfZrlaM";
    private static final String templateId = "ESU_t03_hGtinrMAJDXnnAK1qX_itFXge_mkcOq1zrc";

    @Transactional
    @ApiOperation(value = "登陆绑定云平台用户", notes = "登陆绑定云平台用户")
    @RequestMapping(value = "/bindUser", method = RequestMethod.POST)
    public ResultObject bindUser(@RequestBody BindVxUser bindVxUser) {
        ResultObject resultObject = new ResultObject();

        if (bindVxUser.getUsername() == null || StringUtils.isEmpty(bindVxUser.getUsername())) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("用户名不能为空");
            return resultObject;
        }
        if (bindVxUser.getOpenid() == null || StringUtils.isEmpty(bindVxUser.getOpenid())) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("openid不能为空");
            return resultObject;
        }
        if (bindVxUser.getPassword() != null) {
            bindVxUser.setPassword(MD5Utils.getMD5(bindVxUser.getPassword()));//密码用md5加密
        }

        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUser::getUsername, bindVxUser.getUsername());
        queryWrapper.lambda().eq(SysUser::getPassword, bindVxUser.getPassword());
        SysUser user = userService.getOne(queryWrapper);
        if(user == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("账号或密码错误");
            return resultObject;
        }else{
            //清除之前绑定用户信息  清除send了
//            sysUserVxService.removeById(bindVxUser.getOpenid());

            SysUserVx sysUserVx = new SysUserVx();
            sysUserVx.setOpenid(bindVxUser.getOpenid());
            sysUserVx.setUserid(user.getUserId());

            boolean b = sysUserVxService.saveOrUpdate(sysUserVx);
            if (b) {
                resultObject.setData(user.getUserId());
                resultObject.setCode(HttpServletResponse.SC_OK);
                resultObject.setSuccess(Boolean.TRUE);
                resultObject.setMsg("绑定用户成功");
            } else {
                resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resultObject.setSuccess(Boolean.FALSE);
                resultObject.setMsg("绑定用户失败");
            }
        }

        return resultObject;
    }

    @RequestMapping(value = "/addVxSend", method = RequestMethod.GET)
    @ResponseBody
    public ResultObject addVxSend(String openid,int send) {
        ResultObject resultObject = new ResultObject();

        if(openid == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("openid为空");
            return resultObject;
        }
        SysUserVx sysUserVx = new SysUserVx();
        sysUserVx.setOpenid(openid);
        sysUserVx.setSend(send);
        boolean b = sysUserVxService.saveOrUpdate(sysUserVx);
        if (b) {
            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setSuccess(Boolean.TRUE);
            resultObject.setMsg("更新成功");
        } else {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("更新失败");
        }

        return resultObject;
    }

    @RequestMapping(value = "/updatVxBindUser", method = RequestMethod.GET)
    @ResponseBody
    public ResultObject updatVxBindUser(String openid) {
        ResultObject resultObject = new ResultObject();

        if(openid == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("openid为空");
            return resultObject;
        }
        SysUserVx sysUserVx = new SysUserVx();
        sysUserVx.setOpenid(openid);
        sysUserVx.setUserid(0);
        boolean b = sysUserVxService.saveOrUpdate(sysUserVx);
        if (b) {
            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setSuccess(Boolean.TRUE);
            resultObject.setMsg("更新成功");
        } else {
            resultObject.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("更新失败");
        }

        return resultObject;
    }

    @RequestMapping(value = "/getVxUserId", method = RequestMethod.GET)
    @ResponseBody
    public ResultObject getVxUserId(String openid) {
        ResultObject resultObject = new ResultObject();

        if(openid == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("openid为空");
            return resultObject;
        }
        SysUserVx sysUserVx = sysUserVxService.getById(openid);
        if(sysUserVx == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("用户为空");
            return resultObject;
        }else{
            resultObject.setData(sysUserVx.getUserid());
        }

        return resultObject;
    }

    @RequestMapping(value = "/getVxUser", method = RequestMethod.GET)
    @ResponseBody
    public ResultObject getVxUser(String userid) {
        ResultObject resultObject = new ResultObject();

        if(userid == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("用户id为空");
            return resultObject;
        }
        SysUser user = userService.getById(userid);
        if(user == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("用户为空");
            return resultObject;
        }else{
            UserVo userVo = new UserVo();
            BeanUtils.copyProperties(user,userVo);
            userVo.setPassword(null);
            resultObject.setData(userVo);
        }

        return resultObject;
    }


    /**
     * @param code 用户允许登录后，回调内容会带上 code（有效期五分钟），将 code 换成 openid 和 session_key
     * @return
     */
    @RequestMapping(value = "/decodeUserInfo", method = RequestMethod.GET)
    @ResponseBody
    public ResultObject decodeUserInfo(String code) {
        ResultObject resultObject = new ResultObject();
        // 登录凭证不能为空
        if (code == null || code.length() == 0) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("登录凭证不能为空");
            return resultObject;
        }

        //向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        // 请求参数
        String params = "appid=" + appId + "&secret=" + appsecret + "&js_code=" + code + "&grant_type=authorization_code";

        // 发送请求
        String sr = VXHttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);

        JSONObject jsonObject = JSONObject.parseObject(sr);

        String openid = jsonObject.getString("openid");// 用户唯一标识

        if(openid == null){
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("openid为空");
            return resultObject;
        }else{
            SysUserVx sysUserVx = sysUserVxService.getById(openid);
            int userid = 0;
            if(sysUserVx != null ){
                userid = sysUserVx.getUserid();
            }
            OpenUser openUser = new OpenUser();
            openUser.setOpenid(openid);
            openUser.setUserid(userid);
            resultObject.setCode(HttpServletResponse.SC_OK);
            resultObject.setSuccess(Boolean.TRUE);
            resultObject.setMsg("获取openId成功");
            resultObject.setData(openUser);
        }


        return resultObject;
    }

    /**
     * @param vxUserInfo 用户允许登录后，回调内容会带上 code（有效期五分钟），将 code 换成 openid 和 session_key
     * @return
     */
    @RequestMapping(value = "/deUnionIdUserInfo", method = RequestMethod.POST)
    @ResponseBody
    public ResultObject deUnionIdUserInfo(@RequestBody VxUserInfo vxUserInfo) {
        ResultObject resultObject = new ResultObject();
        // 登录凭证不能为空
        if (vxUserInfo.getCode() == null || vxUserInfo.getCode().length() == 0) {
            resultObject.setCode(HttpServletResponse.SC_BAD_REQUEST);
            resultObject.setSuccess(Boolean.FALSE);
            resultObject.setMsg("登录凭证不能为空");
            return resultObject;
        }
        //小程序的appid
        String appId = "wx9aa24dedf8019fcd";
        // 小程序的secret
        String appsecret = "721b8925ad96548dfac0a36735c635e0";

        //向微信服务器 使用登录凭证 code 获取 session_key 和 openid
        // 请求参数
        String params = "appid=" + appId + "&secret=" + appsecret + "&js_code=" + vxUserInfo.getCode() + "&grant_type=authorization_code";

        // 发送请求
        String sr = VXHttpRequest.sendGet("https://api.weixin.qq.com/sns/jscode2session", params);

        JSONObject jsonObject = JSONObject.parseObject(sr);

        String openId = jsonObject.getString("openid");// 用户唯一标识

        String session_key = jsonObject.getString("session_key");// 密钥

        JSONObject result = decryptionUserInfo(session_key,vxUserInfo.getEncryptedData(),vxUserInfo.getIv());
        System.out.println(result);

//        System.out.println(jsonObject);
//        System.out.println(jsonObject.get("openid"));
//
        resultObject.setCode(HttpServletResponse.SC_OK);
        resultObject.setSuccess(Boolean.TRUE);
        if(result.get("unionId") == null){
            resultObject.setMsg("获取unionId失败");
        }else{
            resultObject.setMsg("获取unionId成功");
            resultObject.setData(result.get("unionId"));
        }


        return resultObject;
    }

    /**
     * 小程序解密用户数据
     *
     * @param encryptedData
     * @param sessionKey
     * @param iv
     * @return
     */
    private static JSONObject decryptionUserInfo(String sessionKey,String encryptedData, String iv) {
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey.getBytes());
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData.getBytes());
        // 偏移量
        byte[] ivByte = Base64.decode(iv.getBytes());

        try {
            // 如果密钥不足16位，那么就补足. 这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);// 初始化
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                String result = new String(resultByte, "UTF-8");
                return JSONObject.parseObject(result);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //改为有工单直接推送
//    @Scheduled(cron = "0 0 15 * * ?")
//    @PostConstruct
    public void sendMessage(){
        QueryWrapper<SysUserVx> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(SysUserVx::getSend,1);
        List<SysUserVx> list = sysUserVxService.list(queryWrapper);
        for(SysUserVx sysUserVx : list){
            LocalDateTime startTime = LocalDateTime.now();
            startTime = startTime.plusDays(-1);
            LocalDateTime endTime = LocalDateTime.now();
            QueryWrapper<Order> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.lambda().eq(Order::getOrderStatus,"未处理");
            queryWrapper1.lambda().eq(Order::getHandlerId,sysUserVx.getUserid());
            queryWrapper1.between("create_time",startTime,endTime);
            int count = orderService.count(queryWrapper1);
            if(count > 0){
                send(sysUserVx.getOpenid());
            }
        }
    }

    private void send(String openid){
//        String data = "{\"touser\":\"oSTPt4u-ZdnfuP8jG-o2sjpJxemQ\",\"data\":{\"date1\":\"{\\\"value\\\":\\\"2020-10-10\\\"}\",\"thing2\":\"{\\\"value\\\":\\\"您好，您有新的工单未处理，请及时查看\\\"}\"},\"template_id\":\"lNYItu_BpH78SXkloMOYsV1ihSiP-f9spmGgkfZrlaM\"}";
        JSONObject templateData = new JSONObject(16, true);
        JSONObject date1 = new JSONObject();
        System.out.println(LocalDate.now()+" "+ LocalTime.now());
        date1.put("value", LocalDate.now());
        templateData.put("date1",date1.toJSONString());

        JSONObject thing2 = new JSONObject();
        thing2.put("value", "您好，您有新的工单未处理，请及时查看");
        templateData.put("thing2",thing2.toJSONString());

        JSONObject args = new JSONObject();
        args.put("touser", openid);
        args.put("template_id", templateId);
        args.put("data",templateData);

        System.out.println(args.toJSONString());
        String result = VXHttpRequest.sendPost("https://api.weixin.qq.com/cgi-bin/message/subscribe/send?access_token="+getAccessToken(),args.toJSONString());
        System.out.println(result);
    }

    private String getAccessToken(){//expires_in 凭证有效时间，单位：秒。目前是7200秒之内的值。
        // 请求参数
        String params = "appid=" + appId + "&secret=" + appsecret + "&grant_type=client_credential";

        // 发送请求
        String sr = VXHttpRequest.sendGet("https://api.weixin.qq.com/cgi-bin/token", params);

        JSONObject jsonObject = JSONObject.parseObject(sr);

        System.out.println(jsonObject);
        return jsonObject.getString("access_token");
    }


    @ApiOperation(value = "下载图片", notes = "下载文件")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    @ResponseBody
    public String download(String filename, String filepath, HttpServletResponse response) throws Exception {

        try {
            if (filename != null) {
                File file = new File(filepath);
                // 如果文件存在，则进行下载
                if (file.exists()) {
                    FileUploadUtils.downloadFile(filename, filepath, response);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    @ApiOperation("上传图片")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResultObject upload(@RequestParam(name = "iacfile", required = false) MultipartFile iacfile) {
        ResultObject res = new ResultObject();
        String filePath = FileUploadUtils.upload(iacfile);
        return res.data(filePath);
    }

    @ApiOperation("新闻漏洞")
    @RequestMapping(value = "/insertLoop", method = RequestMethod.POST)
    public ResultObject insertLoop(@RequestBody LoopHoleVx loopHoleVx) {
        ResultObject res = new ResultObject();
        boolean b = iLoopHoleVxService.save(loopHoleVx);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setData(b);
            res.setMsg("上传成功");
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("上传失败");
        }
        return res;
    }

    @ApiOperation("查询轮动个数")
    @RequestMapping(value = "/selectPictureAutoCount", method = RequestMethod.GET)
    public ResultObject selectPictureAutoCount() {
        QueryWrapper<LoopHoleVx> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LoopHoleVx::getPictureauto,1);
       int count = iLoopHoleVxService.count(queryWrapper);

        return ResultObject.data(count);
    }

    @ApiOperation("查询轮动")
    @RequestMapping(value = "/selectPicture", method = RequestMethod.GET)
    public ResultObject selectPicture() {
        QueryWrapper<LoopHoleVx> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LoopHoleVx::getPictureauto,1);
        queryWrapper.lambda().orderByDesc(LoopHoleVx::getCreateTime);
        List<LoopHoleVx> list = iLoopHoleVxService.list(queryWrapper);

        return ResultObject.data(list);
    }

    @ApiOperation("查询漏洞")
    @RequestMapping(value = "/selectLoop", method = RequestMethod.GET)
    public ResultObject selectLoop(PageParam pageParam) {
        QueryWrapper<LoopHoleVx> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LoopHoleVx::getPictureauto,0);
        queryWrapper.lambda().orderByDesc(LoopHoleVx::getCreateTime);
        IPage<LoopHoleVx> page = iLoopHoleVxService.page(pageParam.iPage(), queryWrapper);

        return ResultObject.data(page);
    }

    @ApiOperation("更新漏洞")
    @RequestMapping(value = "/updateLoop", method = RequestMethod.POST)
    public ResultObject updateLoop(@RequestBody LoopHoleVx loopHoleVx) {
        ResultObject res = new ResultObject();
        boolean b = iLoopHoleVxService.saveOrUpdate(loopHoleVx);
        if (b) {
            res.setCode(HttpServletResponse.SC_OK);
            res.setSuccess(Boolean.TRUE);
            res.setData(b);
            res.setMsg("更新成功");
        } else {
            res.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            res.setSuccess(Boolean.FALSE);
            res.setMsg("更新失败");
        }
        return res;
    }
}
