package com.jwell56.security.cloud.service.ids.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author wsg
 * @since 2020-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("sys_filed_setting")
public class FiledSetting extends Model<FiledSetting> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "filed_set_id", type = IdType.AUTO)
    private Integer filedSetId;

    private Integer userId;

    private String type;

    private Integer selfAdaption;

    private String filed;

    private Integer page;

    private LocalDateTime createTime;


    @Override
    protected Serializable pkVal() {
        return this.filedSetId;
    }

}
