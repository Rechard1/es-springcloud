package com.jwell56.security.cloud.service.role.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author RonnieXu
 * @since 2019-04-15
 */
@Data
@Accessors(chain = true)
public class RoleModuleVo implements Serializable {

    private static final long serialVersionUID = 1L;

//    /**
//     * moduleId
//     */
//    private Integer roleModuleId;

    /**
     * modulePId
     */
    private Integer pId;

    /**
     * level
     */
    private Integer level;

    /**
     * 角色ID，一个
     */
//    private Integer roleId;

    /**
     * 模块名称
     */
    private String moduleName;

    /**
     * 模块ID
     */
    private Integer moduleId;

    /**
     * 模块访问地址
     */
    private String basePath;

}
