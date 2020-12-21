package com.jwell56.security.cloud.service.sso.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色单位表
 * </p>
 *
 * @author wsg
 * @since 2019-11-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_role_unit")
public class RoleUnit extends Model<RoleUnit> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "role_unit_id",type= IdType.AUTO)
    private int roleUnitId;
    /**
     * 角色ID，一个
     */
    private Integer roleId;

    /**
     * 功能模块ID（一个或者多个）
     */
    private Integer unitId;

    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
