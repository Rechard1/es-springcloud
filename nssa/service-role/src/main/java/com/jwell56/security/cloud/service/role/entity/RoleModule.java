package com.jwell56.security.cloud.service.role.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

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
@TableName("sys_role_module")
public class RoleModule extends Model<RoleModule> implements Serializable {

    private static final long serialVersionUID = 1L;

    //对应id，可不填
    @TableId(value = "role_module_id",type= IdType.AUTO)
    private int roleModuleId;

    /**
     * 角色ID，一个
     */
    private Integer roleId;

    /**
     * 功能模块ID（一个或者多个）
     */
    private Integer moduleId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
