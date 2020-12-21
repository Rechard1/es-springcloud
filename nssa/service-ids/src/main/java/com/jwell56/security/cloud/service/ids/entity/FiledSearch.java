package com.jwell56.security.cloud.service.ids.entity;

import com.jwell56.security.cloud.service.ids.annotation.Validation;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wsg
 * @since 2020/12/10
 */
@Data
public class FiledSearch {
    @ApiModelProperty(value = "类型", allowableValues = "idsAlert,idsFlow,idsFileInfo")
    @Validation(allowEmpty = false)
    private String type;

    @ApiModelProperty(value = "用户id")
    @Validation(allowEmpty = false)
    private Integer userId;
}
