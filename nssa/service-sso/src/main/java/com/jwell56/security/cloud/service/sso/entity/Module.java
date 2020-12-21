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
 * 功能模块表
 * </p>
 *
 * @author wsg
 * @since 2019-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_module")
public class Module extends Model<Module> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "module_id",type= IdType.AUTO)
    private int moduleId;
    /**
     * 子级模块的父ID
     */
    private Integer pId;

    private String moduleName;

    /**
     * 表示最底层的新增，修改和详情页面模块，这里统一先用4表示
     */
    private Integer level;

    private String basePath;

    /**
     * 模块备注说明
     */
    private String remark;

    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return null;
    }

}
