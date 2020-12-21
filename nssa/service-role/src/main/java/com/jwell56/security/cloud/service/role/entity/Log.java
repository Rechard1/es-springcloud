package com.jwell56.security.cloud.service.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author wsg
 * @since 2019-04-12
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_operation_log")//TODO 临时改了表，之前是sys_log
public class Log implements Serializable {
    private static final long serialVersionUID = 1L;

    //对应id，可不填
    @TableId(value = "log_id",type= IdType.AUTO)
    private int logId;

    private String handleModule;

    private String handleUser;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    private String requestUrl;

    private String remoteAddr;

    private String httpMethod;

    private String classMethod;

    private String handleType;

    private String handleDes;

    public static final String HANDLE_MODULE_LOGIN="权限管理模块";
    public static final String HANDLE_TYPE_LOGIN ="登录";
    public static final String HANDLE_TYPE_LOGOUT ="登出";
}
