package com.jwell56.security.cloud.service.netstruct.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
@TableName("sys_role_area")
public class RoleArea extends Model<RoleArea> implements Serializable {

    private static final long serialVersionUID = 1L;

    //对应id，可不填
    @TableId(value = "role_area_id",type= IdType.AUTO)
    private int roleAreaId;
    /**
     * 角色ID，一个
     */
    private Integer roleId;

    /**
     * 功能模块ID（一个或者多个）
     */
    private Integer areaId;

    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
