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
 * 功能接口表
 * </p>
 *
 * @author wsg
 * @since 2019-11-16
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_module_interface")
public class ModuleInterface extends Model<ModuleInterface> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "module_interface_id",type= IdType.AUTO)
    private int moduleInterfaceId;
    /**
     * 模块id
     */
    private Integer moduleId;

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
