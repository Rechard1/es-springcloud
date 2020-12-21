package com.jwell56.security.cloud.service.sso.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_user") //对应表名
public class UserDto {

    @ApiModelProperty("code")
    private String code;

    @ApiModelProperty("username")
    private String username;

    @ApiModelProperty("password")
    private String password;


}
