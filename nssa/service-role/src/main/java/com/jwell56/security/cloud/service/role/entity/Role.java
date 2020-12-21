package com.jwell56.security.cloud.service.role.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.jwell56.security.cloud.service.role.validated.AddRole;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author RonnieXu
 * @since 2019-04-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("sys_role")
public class Role extends Model<Role> implements Serializable {

    private static final long serialVersionUID = 1L;

    //对应id，可不填
    @TableId(value = "role_id",type= IdType.AUTO)
    private int roleId;
    
    private Integer creatorId;

    @NotEmpty(groups = {AddRole.class})
    private String roleName;

    private String roleDesc;
    
    private Integer roleType;
    
    private Integer enterpriseId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
