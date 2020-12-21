package com.jwell56.security.cloud.service.apt.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user")
public class User extends Model<User> {

    private static final long serialVersionUID = 1L;
    
    @TableId(value = "user_id",type= IdType.AUTO)
    private int userId;

    private String userName;

    private String password;

    private String realName;

    private String phone;

    private String email;

    private Integer roleId;
    
    private String roleType;

    private Integer enterpriseId;

    private String remark;

	@Override
	protected Serializable pkVal() {
		// TODO Auto-generated method stub
		return null;
	}

}
